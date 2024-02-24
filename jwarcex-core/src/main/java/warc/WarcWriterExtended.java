/**
 * Provides a WarcWriter subclass than writes extended header fields.
 *
 * The method "writeHeader_impl(WarcRecord record)" has been adopted
 * and adapted from the original WarcWriter implementation. This file,
 * along with any additional modifications, is released under
 * APACHE-2.0 license. The original license header is shown
 * in the following.
 */
/**
 * Java Web Archive Toolkit - Software to read and validate ARC, WARC
 * and GZip files. (http://jwat.org/)
 * Copyright 2011-2012 Netarkivet.dk (http://netarkivet.dk/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package warc;

import org.jwat.common.HeaderLine;
import org.jwat.warc.WarcConcurrentTo;
import org.jwat.warc.WarcConstants;
import org.jwat.warc.WarcDate;
import org.jwat.warc.WarcRecord;
import org.jwat.warc.WarcWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class WarcWriterExtended extends WarcWriter {

    @Override
    protected byte[] writeHeader_impl(WarcRecord record) throws IOException {
        header = record.header;
        headerContentLength = header.contentLength;
        if (headerContentLength == null && header.contentLengthStr != null) {
            try {
                headerContentLength = Long.parseLong(header.contentLengthStr);
            } catch (NumberFormatException e) {
                // TODO Add warning...
            }
        }
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        /*
         * Version Line
         */
        byte[] magicVersion = (WarcConstants.WARC_MAGIC_HEADER + header.major + "." + header.minor + "\r\n").getBytes();
        outBuf.write(magicVersion);
        /*
         * Warc-Type
         */
        String warcTypeStr = null;
        if (header.warcTypeIdx != null) {
            if (header.warcTypeIdx > 0
                    && header.warcTypeIdx < WarcConstants.RT_IDX_STRINGS.length) {
                warcTypeStr = WarcConstants.RT_IDX_STRINGS[header.warcTypeIdx];
            } else {
                // Warning...
            }
        }
        if (warcTypeStr == null) {
            warcTypeStr = header.warcTypeStr;
        }
        if (warcTypeStr != null) {
            outBuf.write(WarcConstants.FN_WARC_TYPE.getBytes());
            outBuf.write(": ".getBytes());
            outBuf.write(warcTypeStr.getBytes());
            outBuf.write("\r\n".getBytes());
        }
        /*
         * Warc-Record-Id
         */
        String warcRecordIdStr = null;
        if (header.warcRecordIdUri != null) {
            warcRecordIdStr = header.warcRecordIdUri.toString();
        } else if (header.warcRecordIdStr != null) {
            warcRecordIdStr = header.warcRecordIdStr;
            // Warning...
        }
        if (warcRecordIdStr != null) {
            outBuf.write(WarcConstants.FN_WARC_RECORD_ID.getBytes());
            outBuf.write(": <".getBytes());
            outBuf.write(warcRecordIdStr.getBytes());
            outBuf.write(">\r\n".getBytes());
        }
        /*
         * Warc-Date
         */
        String warcDateStr = null;
        if (header.warcDate != null) {
            // FIXME Check..
            //warcDateStr = WarcDateParser.getDateFormat().format(header.warcDate);
            if (header.major == 1 && header.minor == 0 && header.warcDate.precision != WarcDate.P_SECOND) {
                header.warcDate.adjustPrecisionTo(WarcDate.P_SECOND);
            }
            warcDateStr = header.warcDate.toString();
        } else if (header.warcDateStr != null) {
            warcDateStr = header.warcDateStr;
            // Warning...
        }
        if (warcDateStr != null) {
            outBuf.write(WarcConstants.FN_WARC_DATE.getBytes());
            outBuf.write(": ".getBytes());
            outBuf.write(warcDateStr.getBytes());
            outBuf.write("\r\n".getBytes());
        }
        /*
         * Content-Length
         */
        String contentLengthStr = null;
        if (header.contentLength != null) {
            contentLengthStr = header.contentLength.toString();
        } else if (header.contentLengthStr != null) {
            contentLengthStr = header.contentLengthStr;
            // Warning...
        }
        if (contentLengthStr != null) {
            outBuf.write(WarcConstants.FN_CONTENT_LENGTH.getBytes());
            outBuf.write(": ".getBytes());
            outBuf.write(contentLengthStr.getBytes());
            outBuf.write("\r\n".getBytes());
        }
        /*
         * Content-Type
         */
        String contentTypeStr = null;
        if (header.contentType != null) {
            contentTypeStr = header.contentType.toString();
        } else if (header.contentTypeStr != null) {
            contentTypeStr = header.contentTypeStr;
            // Warning...
        }
        if (contentTypeStr != null) {
            outBuf.write(WarcConstants.FN_CONTENT_TYPE.getBytes());
            outBuf.write(": ".getBytes());
            outBuf.write(contentTypeStr.getBytes());
            outBuf.write("\r\n".getBytes());
        }
        /*
         * Warc-Concurrent-To
         */
        WarcConcurrentTo warcConcurrentTo;
        String warcConcurrentToStr;
        if (header.warcConcurrentToList != null) {
            for (int i=0; i<header.warcConcurrentToList.size(); ++i) {
                warcConcurrentTo = header.warcConcurrentToList.get(i);
                warcConcurrentToStr = null;
                if (warcConcurrentTo.warcConcurrentToUri != null) {
                    warcConcurrentToStr = warcConcurrentTo.warcConcurrentToUri.toString();
                } else if (warcConcurrentTo.warcConcurrentToStr != null) {
                    warcConcurrentToStr = warcConcurrentTo.warcConcurrentToStr;
                    // Warning...
                }
                if (warcConcurrentToStr != null) {
                    outBuf.write(WarcConstants.FN_WARC_CONCURRENT_TO.getBytes());
                    outBuf.write(": <".getBytes());
                    outBuf.write(warcConcurrentToStr.getBytes());
                    outBuf.write(">\r\n".getBytes());
                }
            }
        }
        /*
         * Warc-Block-Digest
         */
        String warcBlockDigestStr = null;
        if (header.warcBlockDigest != null) {
            warcBlockDigestStr = header.warcBlockDigest.toString();
        } else if (header.warcBlockDigestStr != null) {
            warcBlockDigestStr = header.warcBlockDigestStr;
            // Warning...
        }
        if (warcBlockDigestStr != null) {
            outBuf.write(WarcConstants.FN_WARC_BLOCK_DIGEST.getBytes());
            outBuf.write(": ".getBytes());
            outBuf.write(warcBlockDigestStr.getBytes());
            outBuf.write("\r\n".getBytes());
        }
        /*
         * Warc-Payload-Digest
         */
        String warcPayloadDigestStr = null;
        if (header.warcPayloadDigest != null) {
            warcPayloadDigestStr = header.warcPayloadDigest.toString();
        } else if (header.warcPayloadDigestStr != null) {
            warcPayloadDigestStr = header.warcPayloadDigestStr;
            // Warning...
        }
        if (warcPayloadDigestStr != null) {
            outBuf.write(WarcConstants.FN_WARC_PAYLOAD_DIGEST.getBytes());
            outBuf.write(": ".getBytes());
            outBuf.write(warcPayloadDigestStr.getBytes());
            outBuf.write("\r\n".getBytes());
        }
        /*
         * Warc-Ip-Address
         */
        String warcIpAddress = null;
        if (header.warcInetAddress != null) {
            warcIpAddress = header.warcInetAddress.getHostAddress();
        } else if (header.warcIpAddress != null) {
            warcIpAddress = header.warcIpAddress;
            // Warning...
        }
        if (warcIpAddress != null) {
            outBuf.write(WarcConstants.FN_WARC_IP_ADDRESS.getBytes());
            outBuf.write(": ".getBytes());
            outBuf.write(warcIpAddress.getBytes());
            outBuf.write("\r\n".getBytes());
        }
        /*
         * Warc-Refers-To
         */
        String warcRefersToUriStr = null;
        if (header.warcRefersToUri != null) {
            warcRefersToUriStr = header.warcRefersToUri.toString();
        } else if (header.warcRefersToStr != null) {
            warcRefersToUriStr = header.warcRefersToStr;
            // Warning...
        }
        if (warcRefersToUriStr != null) {
            outBuf.write(WarcConstants.FN_WARC_REFERS_TO.getBytes());
            outBuf.write(": <".getBytes());
            outBuf.write(warcRefersToUriStr.getBytes());
            outBuf.write(">\r\n".getBytes());
        }
        /*
         * Warc-Target-Uri
         */
        String warcTargetUriStr = null;
        if (header.warcTargetUriUri != null) {
            warcTargetUriStr = header.warcTargetUriUri.toString();
        } else if (header.warcTargetUriStr != null) {
            warcTargetUriStr = header.warcTargetUriStr;
            // Warning...
        }
        if (warcTargetUriStr != null) {
            outBuf.write(WarcConstants.FN_WARC_TARGET_URI.getBytes());
            outBuf.write(": ".getBytes());
            outBuf.write(warcTargetUriStr.getBytes());
            outBuf.write("\r\n".getBytes());
        }
        /*
         * Warc-Truncated
         */
        String warcTruncatedStr = null;
        if (header.warcTruncatedIdx != null) {
            if (header.warcTruncatedIdx > 0
                    && header.warcTruncatedIdx < WarcConstants.TT_IDX_STRINGS.length) {
                warcTruncatedStr = WarcConstants.TT_IDX_STRINGS[header.warcTruncatedIdx];
            } else {
                // Warning...
            }
        }
        if (warcTruncatedStr == null) {
            warcTruncatedStr = header.warcTruncatedStr;
        }
        if (warcTruncatedStr != null) {
            outBuf.write(WarcConstants.FN_WARC_TRUNCATED.getBytes());
            outBuf.write(": ".getBytes());
            outBuf.write(warcTruncatedStr.getBytes());
            outBuf.write("\r\n".getBytes());
        }
        /*
         * Warc-Warcinfo-Id
         */
        String warcWarcInfoIdStr = null;
        if (header.warcWarcinfoIdUri != null) {
            warcWarcInfoIdStr = header.warcWarcinfoIdUri.toString();
        } else if (header.warcWarcinfoIdStr != null) {
            warcWarcInfoIdStr = header.warcWarcinfoIdStr;
            // Warning...
        }
        if (warcWarcInfoIdStr != null) {
            outBuf.write(WarcConstants.FN_WARC_WARCINFO_ID.getBytes());
            outBuf.write(": <".getBytes());
            outBuf.write(warcWarcInfoIdStr.getBytes());
            outBuf.write(">\r\n".getBytes());
        }
        /*
         * Warc-Filename
         */
        if (header.warcFilename != null) {
            outBuf.write(WarcConstants.FN_WARC_FILENAME.getBytes());
            outBuf.write(": ".getBytes());
            outBuf.write(header.warcFilename.getBytes());
            outBuf.write("\r\n".getBytes());
        }
        /*
         * Warc-Profile
         */
        String warcProfileStr = null;
        if (header.warcProfileUri != null) {
            warcProfileStr = header.warcProfileUri.toString();
        } else if (header.warcProfileIdx != null) {
            if (header.warcProfileIdx > 0
                    && header.warcProfileIdx < WarcConstants.P_IDX_STRINGS.length) {
                warcProfileStr = WarcConstants.P_IDX_STRINGS[header.warcProfileIdx];
            } else {
                // Warning...
            }
        }
        if (warcProfileStr == null) {
            warcProfileStr = header.warcProfileStr;
            // Warning...
        }
        if (warcProfileStr != null) {
            outBuf.write(WarcConstants.FN_WARC_PROFILE.getBytes());
            outBuf.write(": ".getBytes());
            outBuf.write(warcProfileStr.getBytes());
            outBuf.write("\r\n".getBytes());
        }
        /*
         * Warc-Identified-Payload-Type
         */
        String warcIdentifiedPayloadTypeStr = null;
        if (header.warcIdentifiedPayloadType != null) {
            warcIdentifiedPayloadTypeStr = header.warcIdentifiedPayloadType.toString();
        } else if (header.warcIdentifiedPayloadTypeStr != null) {
            warcIdentifiedPayloadTypeStr = header.warcIdentifiedPayloadTypeStr;
            // Warning...
        }
        if (warcIdentifiedPayloadTypeStr != null) {
            outBuf.write(WarcConstants.FN_WARC_IDENTIFIED_PAYLOAD_TYPE.getBytes());
            outBuf.write(": ".getBytes());
            outBuf.write(warcIdentifiedPayloadTypeStr.getBytes());
            outBuf.write("\r\n".getBytes());
        }
        /*
         * Warc-Segment-Number
         */
        String warcSegmentNumberStr = null;
        if (header.warcSegmentNumber != null) {
            warcSegmentNumberStr = header.warcSegmentNumber.toString();
        } else if (header.warcSegmentNumberStr != null) {
            warcSegmentNumberStr = header.warcSegmentNumberStr;
            // Warning...
        }
        if (warcSegmentNumberStr != null) {
            outBuf.write(WarcConstants.FN_WARC_SEGMENT_NUMBER.getBytes());
            outBuf.write(": ".getBytes());
            outBuf.write(warcSegmentNumberStr.getBytes());
            outBuf.write("\r\n".getBytes());
        }
        /*
         * Warc-Segment-Origin-Id
         */
        String warcSegmentOriginIdStr = null;
        if (header.warcSegmentOriginIdUrl != null) {
            warcSegmentOriginIdStr = header.warcSegmentOriginIdUrl.toString();
        } else if (header.warcSegmentOriginIdStr != null) {
            warcSegmentOriginIdStr = header.warcSegmentOriginIdStr;
            // Warning...
        }
        if (warcSegmentOriginIdStr != null) {
            outBuf.write(WarcConstants.FN_WARC_SEGMENT_ORIGIN_ID.getBytes());
            outBuf.write(": <".getBytes());
            outBuf.write(warcSegmentOriginIdStr.getBytes());
            outBuf.write(">\r\n".getBytes());
        }
        /*
         * Warc-Segment-Total-Length
         */
        String warcSegmentTotalLengthStr = null;
        if (header.warcSegmentTotalLength != null) {
            warcSegmentTotalLengthStr = header.warcSegmentTotalLength.toString();
        } else if (header.warcSegmentTotalLengthStr != null) {
            warcSegmentTotalLengthStr = header.warcSegmentTotalLengthStr;
            // Warning...
        }
        if (warcSegmentTotalLengthStr != null) {
            outBuf.write(WarcConstants.FN_WARC_SEGMENT_TOTAL_LENGTH.getBytes());
            outBuf.write(": ".getBytes());
            outBuf.write(warcSegmentTotalLengthStr.getBytes());
            outBuf.write("\r\n".getBytes());
        }
        /*
         * WARC-Refers-To-Target-URI
         */
        String warcRefersToTargetUriStr = null;
        if (header.warcRefersToTargetUriUri != null) {
            warcRefersToTargetUriStr = header.warcRefersToTargetUriUri.toString();
        } else if (header.warcRefersToTargetUriStr != null) {
            warcRefersToTargetUriStr = header.warcRefersToTargetUriStr;
        }
        if (warcRefersToTargetUriStr != null) {
            outBuf.write(WarcConstants.FN_WARC_REFERS_TO_TARGET_URI.getBytes());
            outBuf.write(": ".getBytes());
            outBuf.write(warcRefersToTargetUriStr.getBytes());
            outBuf.write("\r\n".getBytes());
        }
        /*
         * Warc-Refers-To-Date
         */
        String warcRefersToDateStr = null;
        if (header.warcRefersToDate != null) {
            //warcRefersToDateStr = WarcDateParser.getDateFormat().format(header.warcRefersToDate);
            // FIXME Check..
            if (header.major == 1 && header.minor == 0 && header.warcRefersToDate.precision != WarcDate.P_SECOND) {
                header.warcRefersToDate.adjustPrecisionTo(WarcDate.P_SECOND);
            }
            warcRefersToDateStr = header.warcRefersToDate.toString();
        } else if (header.warcRefersToDateStr != null) {
            warcRefersToDateStr = header.warcRefersToDateStr;
            // Warning...
        }
        if (warcRefersToDateStr != null) {
            outBuf.write(WarcConstants.FN_WARC_REFERS_TO_DATE.getBytes());
            outBuf.write(": ".getBytes());
            outBuf.write(warcRefersToDateStr.getBytes());
            outBuf.write("\r\n".getBytes());
        }
        /*
         * Extension fields
         */
        String canonicalUrlFieldKey =
                de.uni_leipzig.asv.tools.jwarcex.core.constant.WarcConstants.WET_FIELD_CANONICAL_URL;
        HeaderLine canonicalUrlField = header.getHeader(canonicalUrlFieldKey);
        if (canonicalUrlField != null) {
            outBuf.write(canonicalUrlFieldKey.getBytes());
            outBuf.write(": ".getBytes());
            outBuf.write(canonicalUrlField.value.getBytes());
            outBuf.write("\r\n".getBytes());
        }
        /*
         * End Of Header
         */
        outBuf.write("\r\n".getBytes());
        byte[] headerBytes = outBuf.toByteArray();
        out.write(headerBytes);
        state = S_HEADER_WRITTEN;
        payloadWrittenTotal = 0;
        return headerBytes;
    }
}
