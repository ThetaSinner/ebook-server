[![CircleCI](https://circleci.com/gh/ThetaSinner/ebook-server.svg?style=svg)](https://circleci.com/gh/ThetaSinner/ebook-server)

# ebook-server
Server for managing your ebooks

## Aims of the project

There's already an excellent tool for working with ebooks called [Calibre](https://calibre-ebook.com/). This is unlikely to ever have as many features, but instead
- It's possible to edit metadata for ebooks which are just links to books on the internet.
- Files are stored directly on the file system. This isolates books a bit as opposed to putting them into a database. This means you can store your books in cloud storage without the corruptions you'll experience using a database. (Partial file sync isn't reliable with databases).
- The metadata is in readable format. Nobody likes being locked into a product. Having metadata easy to pick up and move into another product is what makes this tool different.
- As a consequence of having your metadata in a JSON file, it's very easy to version and undo changes to your data.

## Changes

27th of May, 2018 Version 1.2.0
  - Library view now shows the library name and number of books.
  - Added the ability to filter the library based on a custom query format.

20th of May, 2018 Version 1.1.0
  - Added ability to download or redirect to books so that they can be read.
  - Fixed a bug which was preventing upload of many files.
