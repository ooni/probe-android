package org.openobservatory.ooniprobe.utils;

import android.text.format.DateFormat;

import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Locale;

public class FormattingUtils {

    public static String formatStartTime(Date date) {
        return DateFormat.format(
                DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"),
                date
        ).toString();
    }

    public static String formatRunTime(Double runtime) {
        return new DecimalFormat("#0.00").format(runtime);
    }

    public static String formatBootstrap(Double bootstrapTime) {
        return String.format("%.2f s", bootstrapTime);
    }

    public static String getFormattedBridges(Measurement measurement) {
        return String.format("%1$s/%2$s OK", measurement.getTestKeys().obfs4_accessible, measurement.getTestKeys().obfs4_total);
    }

    public static String getFormattedAuthorities(Measurement measurement) {
        return String.format("%1$s/%2$s OK", measurement.getTestKeys().or_port_dirauth_accessible, measurement.getTestKeys().or_port_dirauth_total);
    }

    public static String getDownload(Integer protocol, TestKeys.Summary summary, TestKeys.Simple simple) {
        if (isNdt7(protocol) && summary != null && summary.download != null)
            return scaleAndFormatFractional(summary.download);
        if (simple != null && simple.download != null)
            return scaleAndFormatFractional(simple.download);
        return null;
    }

    public static String getUpload(Integer protocol, TestKeys.Summary summary, TestKeys.Simple simple) {
        if (isNdt7(protocol) && summary != null && summary.upload != null)
            return scaleAndFormatFractional(summary.upload);
        if (simple != null && simple.upload != null)
            return scaleAndFormatFractional(simple.upload);
        return null;
    }

    public static String getPing(Integer protocol, TestKeys.Summary summary, TestKeys.Simple simple) {
        if (isNdt7(protocol) && summary != null && summary.ping != null)
            return String.format(Locale.getDefault(), "%.1f", summary.ping);
        if (simple != null && simple.ping != null)
            return String.format(Locale.getDefault(), "%.1f", simple.ping);
        return null;
    }

    public static String getPacketLoss(Integer protocol, TestKeys.Summary summary, TestKeys.Advanced advanced) {
        if (isNdt7(protocol) && summary != null && summary.retransmit_rate != null)
            return String.format(Locale.getDefault(), "%.3f%%", summary.retransmit_rate * 100);
        if (advanced != null && advanced.packet_loss != null)
            return String.format(Locale.getDefault(), "%.3f%%", advanced.packet_loss * 100);
        return null;
    }

    public static String getMss(Integer protocol, TestKeys.Summary summary, TestKeys.Advanced advanced) {
        if (isNdt7(protocol) && summary != null && summary.mss != null)
            return String.format(Locale.getDefault(), "%.0f", summary.mss);
        if (advanced != null && advanced.mss != null)
            return String.format(Locale.getDefault(), "%.0f", advanced.mss);
        return null;
    }

    public static String getAveragePing(Integer protocol, TestKeys.Summary summary, TestKeys.Advanced advanced) {
        if (isNdt7(protocol) && summary != null && summary.avg_rtt != null)
            return String.format(Locale.getDefault(), "%.1fms", summary.avg_rtt);
        if (advanced != null && advanced.avg_rtt != null)
            return String.format(Locale.getDefault(), "%.1fms", advanced.avg_rtt);
        return null;
    }

    public static String getMaxPing(Integer protocol, TestKeys.Summary summary, TestKeys.Advanced advanced) {
        if (isNdt7(protocol) && summary != null && summary.max_rtt != null)
            return String.format(Locale.getDefault(), "%.1fms", summary.max_rtt);
        if (advanced != null && advanced.max_rtt != null)
            return String.format(Locale.getDefault(), "%.1fms", advanced.max_rtt);
        return null;
    }

    public static String getBitrate(TestKeys.Simple simple) {
        if (simple != null && simple.median_bitrate != null)
            return setFractionalDigits(getScaledValue(simple.median_bitrate));
        return null;
    }

    public static String getPlayoutDelay(TestKeys.Simple simple) {
        if (simple != null && simple.min_playout_delay != null)
            return String.format(Locale.getDefault(), "%.2fs", simple.min_playout_delay);
        return null;
    }

    private static String scaleAndFormatFractional(double value) {
        return setFractionalDigits(getScaledValue(value));
    }

    public static String setFractionalDigits(double value) {
        return String.format(Locale.getDefault(), value < 10 ? "%.2f" : "%.1f", value);
    }

    public static double getScaledValue(double value) {
        if (value < 1000)
            return value;
        else if (value < 1000 * 1000)
            return value / 1000;
        else
            return value / 1000 * 1000;
    }

    public static Boolean isNdt7(Integer protocol) {
        return protocol != null && protocol == 7;
    }
}
