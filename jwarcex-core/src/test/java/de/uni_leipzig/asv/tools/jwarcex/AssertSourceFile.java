package de.uni_leipzig.asv.tools.jwarcex;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;

import org.junit.Assert;

import com.google.common.base.Charsets;

public final class AssertSourceFile {

	public static final void assertNumEntries(int expectedNumEntries, Path sourceFilePath) {

		int numEntries = 0;

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(sourceFilePath.toFile()), Charsets.UTF_8))) {

			String line = null;
			while ((line = reader.readLine()) != null) {

				// this is really naive, but sufficient for unit tests
				if (line.startsWith("<source>")) {

					numEntries++;
				}
			}
		} catch (Exception e) {

			Assert.fail("Exception during assertNumEntries: " + e.toString());
		}

		Assert.assertEquals(expectedNumEntries, numEntries);
	}

}
