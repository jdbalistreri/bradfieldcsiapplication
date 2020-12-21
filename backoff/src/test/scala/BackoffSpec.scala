import org.scalatest._
import backoff.BackoffManager
import com.twitter.conversions.DurationOps._
import com.twitter.util.{Await, Future, Time}

class CallTracker[Req, Rep](response: Rep, maxCallsBeforeReturning: Int = 1) {
  var calledAtMillis: scala.collection.mutable.Seq[Long] = scala.collection.mutable.Seq[Long]()
  var startTime: Option[Time] = None
  def apply(request: Req): Future[Rep] = {
    if (calledAtMillis.isEmpty) { startTime = Some(Time.now) }
    val currDiff = Time.now.diff(startTime.get).inMillis
    calledAtMillis = calledAtMillis :+ currDiff

    if (calledAtMillis.size >= maxCallsBeforeReturning) {
      Future.value(response)
    } else {
      Future.exception(new Exception("Retry this!"))
    }
  }
}

class BackoffSpec extends FunSuite with DiagrammedAssertions {
  test("Should return without backing off when there's an immediate success") {
    val callTracker = new CallTracker[Int, Int](response=10, maxCallsBeforeReturning=1)
    val fnWithBackoff = new BackoffManager[Int, Int](
      fn = callTracker.apply,
      shouldBackoff = Function.const(false),
      initialBackoffDurationMillis = 1,
    )
    val result = Await.result(fnWithBackoff(1))
    assert(result == 10)
    assert(callTracker.calledAtMillis == Seq(0))
  }

  test("Should raise an exception thrown by fn if shouldBackoff=false") {
    val nonRetryableException = new Exception("Don't retry this one!")
    val fnWithBackoff = new BackoffManager[Int, Int](
      fn = Function.const(Future.exception(nonRetryableException)),
      shouldBackoff = Function.const(false),
      initialBackoffDurationMillis = 1,
    )
    val caught = intercept[Exception] {
      Await.result(fnWithBackoff(1))
    }
    assert(caught == nonRetryableException)
  }

  test("should backoff exponentially when no jitter is specified") {
    Time.withCurrentTimeFrozen{ tc =>
      val callTracker = new CallTracker[Int, Int](response=10, maxCallsBeforeReturning=5)
      val fnWithBackoff = new BackoffManager[Int, Int](
        fn = callTracker.apply,
        shouldBackoff = Function.const(true),
        initialBackoffDurationMillis = 20,
      )

      val resultFu = fnWithBackoff(1)

      // advance the time in 1 ms increments
      while (!resultFu.isDefined) {
        Thread.sleep(1)
        tc.advance(1.millis)
      }

      val result = Await.result(resultFu)
      assert(result == 10)
      assert(callTracker.calledAtMillis == Seq(0, 20, 60, 140, 300))
    }
  }

  test("should backoff exponentially with jitter") {
    Time.withCurrentTimeFrozen{ tc =>
      val callTracker = new CallTracker[Int, Int](response=10, maxCallsBeforeReturning=5)
      val fnWithBackoff = new BackoffManager[Int, Int](
        fn = callTracker.apply,
        shouldBackoff = Function.const(true),
        initialBackoffDurationMillis = 20,
        jitterDurationMillis = 10,
        randomSeed = Some(1)
      )

      val resultFu = fnWithBackoff(1)

      // advance the time in 1 ms increments
      while (!resultFu.isDefined) {
        Thread.sleep(1)
        tc.advance(1.millis)
      }

      val result = Await.result(resultFu)
      assert(result == 10)
      assert(callTracker.calledAtMillis == Seq(0, 16, 55, 134, 291))
    }
  }
}
