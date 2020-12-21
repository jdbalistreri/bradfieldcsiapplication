from flask import Flask, request
import datetime
import json
import pytz

app = Flask(__name__)

@app.route('/api/v1/time')
def timezone():
    tz_arg = request.args.get("timezone")
    if not tz_arg:
        timezone = pytz.utc
    else:
        try:
            timezone = pytz.timezone(tz_arg)
        except pytz.exceptions.UnknownTimeZoneError:
            return json.dumps({"error": "invalid timezone"}), 400

    return json.dumps({
        "time": datetime.datetime.now(tz=timezone).strftime("%H:%M:%S"),
        "timezone": str(timezone)
    })

if __name__ == '__main__':
    app.run()
