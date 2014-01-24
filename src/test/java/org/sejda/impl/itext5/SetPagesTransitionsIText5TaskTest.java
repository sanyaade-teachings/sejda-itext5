/*
 * Created on 15/gen/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of sejda-itext5.
 *
 * sejda-itext5 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sejda-itext5 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with sejda-itext5.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.itext5;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.core.service.DefaultTaskExecutionService;
import org.sejda.impl.TestUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.parameter.SetPagesTransitionParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.transition.PdfPageTransition;
import org.sejda.model.pdf.transition.PdfPageTransitionStyle;
import org.sejda.model.task.Task;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;

/**
 * @author Andrea Vacondio
 *
 */
public class SetPagesTransitionsIText5TaskTest extends BaseTaskTest {
    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();
    private SejdaContext context = mock(DefaultSejdaContext.class);
    private SetPagesTransitionParameters parameters = new SetPagesTransitionParameters();
    private Task<SetPagesTransitionParameters> victimTask = new SetPagesTransitionTask();

    @Before
    public void setUp() throws IOException {
        TestUtils.setProperty(victim, "context", context);
        parameters.setCompress(false);
        parameters.setOutputName("outName.pdf");
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.putTransition(1, PdfPageTransition.newInstance(PdfPageTransitionStyle.BOX_OUTWARD, 1, 5));
        parameters.setOverwrite(true);
        parameters.setOutput(getOutputFile());
    }

    @Test
    public void testExecute() throws TaskException, IOException {
        parameters.setSource(getSource());
        doExecute();
    }

    @Test
    public void testExecuteEncrypted() throws TaskException, IOException {
        parameters.setSource(getEncryptedSource());
        doExecute();
    }

    private void doExecute() throws TaskException, IOException {
        parameters.setOutput(getOutputFile());
        when(context.getTask(parameters)).thenReturn((Task) victimTask);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultFile();
        assertCreator(reader);
        assertEquals(4, reader.getNumberOfPages());
        PdfDictionary dictionary = reader.getPageN(1).getAsDict(PdfName.TRANS);
        assertEquals(PdfName.BOX, dictionary.get(PdfName.S));
        assertEquals(PdfName.O, dictionary.get(PdfName.M));
        assertNull(reader.getPageN(2).getAsDict(PdfName.TRANS));
        assertNull(reader.getPageN(3).getAsDict(PdfName.TRANS));
        assertNull(reader.getPageN(4).getAsDict(PdfName.TRANS));
        reader.close();
    }
}
