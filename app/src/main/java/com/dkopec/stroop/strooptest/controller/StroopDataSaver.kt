package com.dkopec.stroop.strooptest.controller

import android.content.Context
import android.graphics.Color
import com.dkopec.stroop.strooptest.model.StroopTestRecord
import java.text.SimpleDateFormat
import java.util.*
import java.io.*
import java.nio.charset.StandardCharsets


class StroopDataSaver(private val context: Context, private val subjectUUID: UUID, private val isTest: Boolean) {

    private val fileFormat = SimpleDateFormat("yyyyMMdd")

    private val dataFormat = SimpleDateFormat("yyyy-MM-dd_HH:MM:ss")

    private val testUUID = UUID.randomUUID()

    private var size = 0

    fun write(record: StroopTestRecord) {
        size++;
        val presentedStimulus = record.presentedState.stimulus
        val line =
            "${dataFormat.format(Date())},$subjectUUID,$testUUID,$isTest,$size," +
                    "${presentedStimulus.displayName},${color(record.presentedState.color)},${record.chosenStimulus.displayName}," +
                    "${record.presentedState.type()},${record.isCorrect()},${record.time}"
        val fileName = "stroop_test_${fileFormat.format(Date())}.csv"

        writeToStorage(fileName, line)
    }

    private fun writeToStorage(fileName: String, line: String) {
        val dir: File
        try {
            dir = File(context.getExternalFilesDir(null), "stroop")
            dir.mkdirs();
            val file = File("${dir.absolutePath}/$fileName")
            val exists = file.exists()
            val writer = BufferedWriter(OutputStreamWriter(FileOutputStream(file, true), StandardCharsets.UTF_8))
            if (!exists) {
                val header =
                    "czas,sesja,próba,test,lp,bodziec_nazwa,bodziec_kolor,reakcja_kolor,zgodnosc_bodzca, poprawność_reakcji,czas_reakcji_ms"
                writer.appendln(header)
            }
            writer.appendln(line)
            writer.close()
        } catch (e: IOException) {
            throw java.lang.RuntimeException(e)
        }
    }

    fun size() = size

    private fun color(color: Int): String {
        return when (color) {
            Color.WHITE -> "biały"
            Color.RED -> "czerwony"
            Color.YELLOW -> "żółty"
            Color.GREEN -> "zielony"
            Color.BLUE -> "niebieski"
            else -> throw RuntimeException("uknown color $color")
        }
    }

}