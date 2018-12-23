package com.dkopec.stroop.strooptest.controller

import android.content.Context
import android.graphics.Color
import com.dkopec.stroop.strooptest.model.StroopTestRecord
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import java.text.SimpleDateFormat
import java.util.*
import java.io.*
import java.nio.charset.StandardCharsets
import kotlin.collections.ArrayList


class StroopDataSaver(private val context: Context, private val subjectUUID: UUID, private val isTest: Boolean) {

    private val fileFormat = SimpleDateFormat("yyyyMMdd")

    private val dataFormat = SimpleDateFormat("yyyy-MM-dd_HH:MM:ss")

    private val testUUID = UUID.randomUUID()


    private val records = ArrayList<StroopTestRecord>()

    fun write(record: StroopTestRecord) {
        records.add(record)
        val presentedStimulus = record.presentedState.stimulus
        val line =
            "${dataFormat.format(Date())},$subjectUUID,$testUUID,$isTest,${size()}," +
                    "${presentedStimulus.displayName},${color(record.presentedState.color)},${record.chosenStimulus.displayName}," +
                    "${record.presentedState.type()},${record.isCorrect()},${record.time}"
        val fileNameGrouped = "stroop_test_detailed_${fileFormat.format(Date())}.csv"
        val fileNamePerPerson = "stroop_test_detailed_${subjectUUID}_${fileFormat.format(Date())}.csv"

        val header =
            "czas,sesja,próba,test,lp,bodziec_nazwa,bodziec_kolor,reakcja_kolor,zgodnosc_bodzca, poprawność_reakcji,czas_reakcji_ms"
        writeToStorage(fileNameGrouped, line, header)
        writeToStorage(fileNamePerPerson, line, header)
    }

    fun writeSummary() {
        val recordsByStimuliType = records.groupBy { it.presentedState.type() }
        val header =
            "czas,sesja,próba,zgodnosc_bodzca,poprawność_reakcji,sredni_czas_popr,std_czas_popr,sredni_czas,std_czas"

        val fileNameGrouped = "stroop_test_results_${fileFormat.format(Date())}.csv"
        val fileNamePerPerson = "stroop_test_results_${subjectUUID}_${fileFormat.format(Date())}.csv"

        val now = Date()
        for (filename in arrayOf(fileNameGrouped, fileNamePerPerson)) {
            val allResults = summaryString(summary(records), "A", now)
            val neutralResults = summaryString(summary(recordsByStimuliType["N"]!!), "N", now)
            val conformingResults = summaryString(summary(recordsByStimuliType["C"]!!), "C", now)
            val nonConformingResults = summaryString(summary(recordsByStimuliType["I"]!!), "I", now)
            for (line in arrayOf(allResults, neutralResults, conformingResults, nonConformingResults)) {
                writeToStorage(filename, line, header)
            }
        }
    }

    private fun summaryString(summary: TestSummary, group: String, date: Date): String {
        return "${dataFormat.format(Date())},$subjectUUID,$testUUID,$group,${summary.correctness},${safeValue(summary.correctAvgTime)},${safeValue(
            summary.correctTimeStd
        )},${summary.avgTime},${summary.timeStd}"
    }

    private fun safeValue(value: Double): String {
        if (value.isNaN()) {
            return "";
        } else {
            return value.toString();
        }
    }

    private fun summary(records: List<StroopTestRecord>): TestSummary {
        val timeStats = DescriptiveStatistics()
        val correctTimeStats = DescriptiveStatistics()
        val correctRecords = records.filter { it.isCorrect() }
        records.forEach { timeStats.addValue(it.time.toDouble()) }
        correctRecords.forEach { timeStats.addValue(it.time.toDouble()) }
        val correctness = 100.0 * correctRecords.size / records.size
        return TestSummary(
            timeStats.mean,
            timeStats.standardDeviation,
            correctness,
            correctTimeStats.mean,
            correctTimeStats.standardDeviation
        )
    }

    private fun writeToStorage(fileName: String, line: String, header: String) {
        val dir: File
        try {
            dir = File(context.getExternalFilesDir(null), "stroop")
            dir.mkdirs();
            val file = File("${dir.absolutePath}/$fileName")
            val exists = file.exists()
            val writer = BufferedWriter(OutputStreamWriter(FileOutputStream(file, true), StandardCharsets.UTF_8))
            if (!exists) {
                writer.appendln(header)
            }
            writer.appendln(line)
            writer.close()
        } catch (e: IOException) {
            throw java.lang.RuntimeException(e)
        }
    }

    fun size() = records.size

    private fun color(color: Int): String {
        return when (color) {
            Color.WHITE -> "biały"
            Color.RED -> "czerwony"
            Color.YELLOW -> "żółty"
            Color.GREEN -> "zielony"
            Color.BLUE -> "niebieski"
            else -> throw RuntimeException("unknown color $color")
        }
    }

    private data class TestSummary(
        val avgTime: Double,
        val timeStd: Double,
        val correctness: Double,
        val correctAvgTime: Double,
        val correctTimeStd: Double
    )

}