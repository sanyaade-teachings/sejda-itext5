/*
 * Created on 16/gen/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of sejda-itext5.
 *
 * sejda-itext5 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sejda-itext5 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with sejda-itext5.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.itext5.component;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.model.PopulatedFileOutput;
import org.sejda.impl.TestUtils;
import org.sejda.model.exception.TaskException;

import com.itextpdf.text.pdf.PdfReader;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfUnpackerTest {

    private PdfUnpacker victim = new PdfUnpacker(true);
    private InputStream is;
    private MultipleOutputWriter outputWriter;

    @Before
    public void setUp() {
        is = getClass().getClassLoader().getResourceAsStream("pdf/attachments.pdf");
        outputWriter = spy(OutputWriters.newMultipleOutputWriter(true));
        TestUtils.setProperty(victim, "outputWriter", outputWriter);
    }

    @After
    public void tearDown() {
        IOUtils.closeQuietly(is);
    }

    @Test
    public void testUnpack() throws IOException, TaskException {
        PdfReader reader = new PdfReader(is);
        victim.unpack(reader);
        verify(outputWriter).addOutput(any(PopulatedFileOutput.class));
    }

    @Test(expected = TaskException.class)
    public void testUnpackNulll() throws TaskException {
        victim.unpack(null);
    }
}
