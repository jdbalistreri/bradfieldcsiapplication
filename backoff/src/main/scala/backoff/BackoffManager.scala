package backoff

import scala.math.pow
import scala.util.Random
import com.twitter.conversions.DurationOps._
import com.twitter.util.{Future, FuturePool, Return, Throw, Time}

case class ShouldBackoffParams(
  throwable: Throwable,
  previousAttempts: Int
)

class BackoffManager[Req, Rep](
  fn: Req => Future[Rep],
  shouldBackoff: ShouldBackoffParams => Boolean,
  initialBackoffDurationMillis: Int,
  jitterDurationMillis: Int = 0,
  randomSeed: Option[Long] = None,
) {
  private [this] val rand = randomSeed match {
    case Some(seed) => new Random(seed)
    case _ => new Random
  }

  def apply(req: Req): Future[Rep] = callWithBackoff(req, 0)

  private[this] def callWithBackoff(
    req: Req,
    previousAttempts: Int
  ): Future[Rep] = FuturePool.unboundedPool {
      val backoffDurationMillis = if (previousAttempts == 0) {
        0
      } else {
        initialBackoffDurationMillis * pow(2, previousAttempts - 1)
      }
      val calculatedJitterMillis = jitterDurationMillis * rand.nextFloat() - jitterDurationMillis / 2

      Time.sleep((backoffDurationMillis + calculatedJitterMillis).toInt.milliseconds)
    }.flatMap { _ => fn(req).liftToTry.flatMap {
      case Return(v) => Future.value(v)
      case Throw(e) =>
        if (shouldBackoff(ShouldBackoffParams(e, previousAttempts))) {
          callWithBackoff(req, previousAttempts + 1)
        } else {
          throw e
        }
    }
  }
}
