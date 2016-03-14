import ch.qos.logback.core.*;
import ch.qos.logback.core.encoder.*;
import ch.qos.logback.core.read.*;
import ch.qos.logback.core.rolling.*;
import ch.qos.logback.core.status.*;
import ch.qos.logback.classic.net.*;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import static ch.qos.logback.classic.Level.DEBUG;


appender("UTL_SERVER", FileAppender) {
  file = "/var/log/utls/utls.log"
  append = true
  encoder(PatternLayoutEncoder) {
    pattern = "%d{yyyy:MM:dd HH:mm:ss.SSS,GMT} %level - %msg%n"
  }
}
logger("utlserver",DEBUG,["UTL_SERVER"])