package xyz.bluspring.dpfabricifier

import java.io.File
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import java.util.zip.ZipFile
import kotlin.io.path.Path

object Main {
    @JvmStatic
    fun main(args: Array<out String>) {
        if (args.isEmpty()) {
            throw IllegalArgumentException("No path to datapack ZIP given!")
        }

        val workDir = System.getProperty("user.dir")
        val paths = args.map { Path(workDir, it) }

        val template = this::class.java.getResource("/fabric.mod.json")!!.readText()

        paths.forEach {
            val file = it.toFile()
            if (!file.exists()) {
                println("File path $it doesn't exist! Skipping..")
                return@forEach
            }

            val zipFile = ZipFile(file)
            val uuid = UUID.randomUUID()
            val fabricModJson = String.format(template, "dpfabricifier_$uuid", file.nameWithoutExtension)

            val jarFile = File(workDir, file.nameWithoutExtension + ".jar")

            val jarOutput = JarOutputStream(jarFile.outputStream())

            jarOutput.putNextEntry(JarEntry("fabric.mod.json"))
            jarOutput.write(fabricModJson.toByteArray())
            jarOutput.closeEntry()

            for (entry in zipFile.entries()) {
                jarOutput.putNextEntry(entry)
                jarOutput.write(zipFile.getInputStream(entry).readAllBytes())
                jarOutput.closeEntry()
            }

            jarOutput.close()

            println("Wrote ${jarFile.path}")
        }
    }
}