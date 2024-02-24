/**
 * Provides a WarcWriterExtendedUncompressed subclass for the
 * WarcWriterExtended implementation.
 *
 * The methods from this class have been adopted and adapted from
 * the original WarcWriterUncompressed implementation. This file,
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

import org.jwat.warc.WarcRecord;

import java.io.IOException;
import java.io.OutputStream;

public class WarcWriterExtendedUncompressed extends WarcWriterExtended {

    public WarcWriterExtendedUncompressed(OutputStream out) {
        if (out == null) {
            throw new IllegalArgumentException("The 'out' parameter is null!");
        } else {
            this.out = out;
            this.init();
        }
    }

    public boolean isCompressed() {
        return false;
    }

    public void close() throws IOException {
        if (this.state == 1 || this.state == 2) {
            this.closeRecord();
        }

        if (this.out != null) {
            this.out.flush();
            this.out.close();
            this.out = null;
        }

    }

    public void closeRecord() throws IOException {
        if (this.state != 1 && this.state != 2) {
            if (this.state == 0) {
                throw new IllegalStateException("Please write a record before closing it!");
            }
        } else {
            this.closeRecord_impl();
            this.state = 3;
        }

    }

    public byte[] writeHeader(WarcRecord record) throws IOException {
        if (record == null) {
            throw new IllegalArgumentException("The 'record' parameter is null!");
        } else if (this.state == 1) {
            throw new IllegalStateException("Headers written back to back!");
        } else {
            if (this.state == 2) {
                this.closeRecord_impl();
            }

            return this.writeHeader_impl(record);
        }
    }
}
