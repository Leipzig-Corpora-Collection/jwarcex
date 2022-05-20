# Changelog

## [next] - unreleased

- Raise compile target to Java 11.
- Prepare jwarcex for public release.

## [3.0.1] - 16.12.2021

- Refactor text extraction interface.
- Raised log4j version to 2.16.0.

## [2.0.0] - 16.07.2019

### Fixed

- Streaming mode works correctly again. (Most notable in combination with piping on the command line.)

### Changed
- *Jwarcex now requires Java 8.*
- Improved whitespace handling so that the same text is extracted from equivalent html, regardless of the html code's indentation. As a byproduct this reduces the amount of spaces caused by table elements.
- The different threads use a better waiting mechanism. This makes it slightly faster while using less CPU. (Use wait instead of sleep.)
- The text extraction implementation was changed to work iteratively instead of recursively.

## [1.3.1] - 02.04.2019

### Fixed

- Prevent PeekingMetaTagEncodingDetector from returning unavailable charsets.
- Title tag is now handled correctly (and not extracted by default).

### Changed

- Updated jsoup version to 1.11.3.

## [1.3.0] - 26.11.2018

### Added

- Separate consecutive links by a space.

## [1.2.0] - 25.10.2018

### Added

- The detected encoding will be written to the source file headers again.

### Fixed

- The encoding error detection mechanism now correctly checks against the utf-8 character (instead of utf-16).
- The description of command line parameter -e was corrected.
- Fixed html in javadocs of TextExtractorImpl.

## [1.1.0] - 23.10.2017

### Added

- Added auto detection of encoding errors with a configurable threshold (-e or --max_encoding_errors. If a document contains more than 3 replacement chars, it will be dropped.
- Added parameter for minimum document length (-n or --document_length). See the program help (-h) for more details.
- Ignore entries ending with robots.txt.

### Changed

- Make the minimum line length parameter accessible via the command line (-m or --line_length).
- Improved handling of tables, which results in more readable output of table cell text.

### Fixed

- Fixed the usage of the maven-shade-plugin. The resulting jwarcex-standalone*.jar can now be again used with the `java -jar jwarcex-standalone*.jar` syntax.
