package ru.bokhonin.montanaktl

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Vibrator
import java.text.SimpleDateFormat
import java.util.*

class MontanaService_ : IntentService("MontanaService_") {

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {

            val sharedPreferences = getSharedPreferences("montana_preferences", 0)
            val vibration = sharedPreferences.getBoolean("mVibrationSwitch", true)
            val sound = sharedPreferences.getBoolean("mSoundSwitch", false)

            var contentText: String

            contentText = if (vibration) "Montana. Vbr: On." else "Montana. Vbr: Off."
            contentText += if (sound) " Snd: On." else " Snd: Off."


            val timeStart = nextTime
            val cal = Calendar.getInstance()
            cal.timeInMillis = timeStart

            val dateFormat = SimpleDateFormat()
            val dateString = dateFormat.format(cal.time)
            contentText += " Next: " + dateString

            val pi = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0)

            val notification = Notification.Builder(this)
                    .setTicker("Test Mont")
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle("TEST Montana")
                    .setContentText(contentText)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build()

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.notify(0, notification)

            if (sound) {
                val mPlayer = MediaPlayer.create(this, R.raw.signal)
                mPlayer.start()
            }

            if (vibration) {
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                val pattern = LongArray(6)
                pattern[0] = 0
                pattern[1] = 200
                pattern[2] = 100
                pattern[5] = 600

                vibrator.vibrate(pattern, -1)
            }

            setServiceAlarm(this, true)
        }
    }

    companion object {

        fun setServiceAlarm(context: Context, isOn: Boolean) {

            val intent = Intent(context, MontanaService_::class.java)
            val pendingIntent = PendingIntent.getService(context, 0, intent, 0)

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (isOn) {
                // В первом варианте использовалась эта функция, но тесты и последующее вдумчивое чтение документации
                // показало, что данная функция не дает точные вызовы (из-за энергосбережения)
                // alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 60000, pendingIntent);

                // Установим звуковой сигнал на срабатывание на ближайший час
                // Сделаем так, чтобы ночью сигнал не срабатывал (с 21 до 9)
                val timeStart = nextTime
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeStart, pendingIntent)

            } else {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }

        fun isServiceAlarmOn(context: Context): Boolean {

            val intent = Intent(context, MontanaService_::class.java)
            val pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE)

            return pendingIntent != null
        }

        private val nextTime: Long
            get() {

                val rightNowPlusHour = Calendar.getInstance()
                rightNowPlusHour.set(Calendar.MINUTE, 0)
                rightNowPlusHour.set(Calendar.SECOND, 0)
                rightNowPlusHour.set(Calendar.MILLISECOND, 0)
                rightNowPlusHour.add(Calendar.HOUR, 1)

                val currentHour = rightNowPlusHour.get(Calendar.HOUR)
                val currentAmPm = rightNowPlusHour.get(Calendar.AM_PM)
                val currentMillis = rightNowPlusHour.timeInMillis
                val rightNowMs = Calendar.getInstance().timeInMillis
                val durationSeconds = currentMillis - rightNowMs
                var hourPlus = 0

                if (currentAmPm == 0 && currentHour <= 9) {
                    hourPlus = 9 - currentHour
                } else if (currentAmPm == 1 && currentHour >= 9) {
                    hourPlus = 11 - currentHour + 10
                }

                val timeStart = System.currentTimeMillis() + durationSeconds + (hourPlus * 60 * 60 * 1000).toLong()

                return timeStart
            }
    }
}