package cf.youngauthentic.forum.config

import java.io.PrintWriter
import java.io.StringWriter

val Exception.stackTraceString: String
    get() {
        val stringWriter = StringWriter()
        this.printStackTrace(PrintWriter(stringWriter))
        return stringWriter.toString()
    }