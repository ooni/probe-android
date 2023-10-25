package org.openobservatory.ooniprobe.common

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ReplacementSpan
import android.view.View
import android.widget.TextView
import io.noties.markwon.AbstractMarkwonPlugin


/**
 * Read more plugin based on text length.
 * @see <a href="https://github.com/noties/Markwon/blob/v4.6.2/app-sample/src/main/java/io/noties/markwon/app/samples/ReadMorePluginSample.java#L208C2-L208C2">ReadMorePluginSample</a>
 */
class ReadMorePlugin : AbstractMarkwonPlugin() {
    private val maxLength = 150
    private val labelMore = "\n\nRead more >"
    private val labelLess = "\n\nRead less >"

    override fun afterSetText(textView: TextView) {
        val text = textView.text
        if (text.length < maxLength) {
            // everything is OK, no need to ellipsize)
            return
        }
        val breakAt = breakTextAt(text, 0, maxLength)
        val cs = createCollapsedString(text, 0, breakAt)
        textView.text = cs
    }

    private fun createCollapsedString(text: CharSequence, start: Int, end: Int): CharSequence {
        val builder = SpannableStringBuilder(text, start, end)

        // NB! each table row is represented as a space character and new-line (so length=2) no
        //  matter how many characters are inside table cells

        // we can _clean_ this builder, for example remove all dynamic content (like images and tables,
        //  but keep them in full/expanded version)
        if (true) {
            // it is an implementation detail but _mostly_ dynamic content is implemented as
            //  ReplacementSpans
            val spans = builder.getSpans(
                0, builder.length,
                ReplacementSpan::class.java
            )
            if (spans != null) {
                for (span in spans) {
                    builder.removeSpan(span)
                }
            }

            // NB! if there will be a table in _preview_ (collapsed) then each row will be represented as a
            // space and new-line
            trim(builder)
        }
        val fullText = createFullText(text, builder)
        builder.append(" ...")
        val length = builder.length
        builder.append(labelMore)
        builder.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                (widget as TextView).text = fullText
            }
        }, length, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return builder
    }

    private fun createFullText(text: CharSequence, collapsedText: CharSequence): CharSequence {
        // full/expanded text can also be different,
        //  for example it can be kept as-is and have no `collapse` functionality (once expanded cannot collapse)
        //  or can contain collapse feature
        val fullText: CharSequence
        if (true) {
            // for example, let's allow collapsing
            val builder = SpannableStringBuilder(text)
            builder.append(' ')
            val length = builder.length
            builder.append(labelLess)
            builder.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    (widget as TextView).text = collapsedText
                }
            }, length, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            fullText = builder
        } else {
            fullText = text
        }
        return fullText
    }

    companion object {
        private fun trim(builder: SpannableStringBuilder) {

            // NB! tables use `\u00a0` (non breaking space) which is not reported as white-space
            var c: Char
            run {
                var i = 0
                val length = builder.length
                while (i < length) {
                    c = builder[i]
                    if (!Character.isWhitespace(c) && c != '\u00a0') {
                        if (i > 0) {
                            builder.replace(0, i, "")
                        }
                        break
                    }
                    i++
                }
            }
            for (i in builder.length - 1 downTo 0) {
                c = builder[i]
                if (!Character.isWhitespace(c) && c != '\u00a0') {
                    if (i < builder.length - 1) {
                        builder.replace(i, builder.length, "")
                    }
                    break
                }
            }
        }

        // depending on your locale these can be different
        // There is a BreakIterator in Android, but it is not reliable, still theoretically
        //  it should work better than hand-written and hardcoded rules
        private fun breakTextAt(text: CharSequence, start: Int, max: Int): Int {
            var last = start

            // no need to check for _start_ (anyway will be ignored)
            for (i in start + max - 1 downTo start + 1) {
                val c = text[i]
                if (Character.isWhitespace(c) || c == '.' || c == ',' || c == '!' || c == '?') {
                    // include this special character
                    last = i - 1
                    break
                }
            }
            return if (last <= start) {
                // when used in subSequence last index is exclusive,
                //  so given max=150 would result in 0-149 subSequence
                start + max
            } else last
        }
    }
}