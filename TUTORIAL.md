# HANDLE Documentation

# Installing HANDLE
Please remember: HANDLE is in the extreme alpha phase. Please be generous with your bug reports and also with your patience.

First, install Java 12 or later. You can obtain this from [link].

Once Java is installed, download the JAR file [link]. Place it in a convenient location, and then double-click the JAR file to run it.

# Using HANDLE
There are essentially three steps to using HANDLE: importing data, generating topic models, and exporting visualizations and topic data.

## Importing Data
### Bringing in Documents
First, to bring in documents, click the “Data Importing” tab (if it’s not already selected). Then, drag and drop files from the folder they’re inside into the left-handed pane on the window.
![Data import and left-hand pane](./img/topic-models.tab)
HANDLE can import the following types of files:
* plain text files, in UTF-8 encoding (.txt)
* old-style Microsoft Word files (.doc)
* newer Microsoft Word files (.docx)
To see how MALLET will understand the document, click on a document. The document content will show in the middle window. Stop words will appear struck through. If text has been lowercased, all the text will be lowercased.
![Document selected and rendered in window](./img/topic-models.tab)
To remove a document, right click (control-click) on it and then select “Remove”
!["Remove Document" controls](./img/topic-models.tab)

NOTE: adding or removing documents after you have generated topic models will invalidate all your topic models. This will remove all the topic models you have generated. HANDLE will display a warning message if you try to do this.

Can handle work with non-Latin characters?
Yes. However it will not perform word segmentation for any languages automatically.

### Adding stop words
By default, HANDLE has no stop words. To add a stop word list, click the “Add stop words” button. The stop words window will show. Then drag and drop a file with stop words into the window.
![Dropping a stopwords file](./img/topic-models.tab)
This file should be a plain text list of stop words, with one word per line.
You may add as many files as you like. Dragging and dropping two files with different sets of stop words will add *ALL* stop words in both files to the list.
You can also add stop words from the word count list to the side of the document text view. Right click on it and click “Add as stop word …”.
To remove a stop word (that is, to make it *not* a stop word), open the stop words window and right-click on it. A menu will appear that will allow you to remove the stop word from the list.

NOTE: adding or removing stop words after you have generated topic models will invalidate all your topic models. This will remove all the topic models you have generated. HANDLE will display a warning message if you try to do this.

## Running a New Topic Model
First, select the “Topic Models” tab.
![Topic Models Tab](./img/topic-models.png)
Then, to add a new topic model, click the “Add” button at the bottom. The Topic Model settings window will pop up. Enter the appropriate parameters and click “Run.”
![Topic Model Settings window](./img/topic-model-settings.png)
The graphs that pop up will show the topic models over time.

You can generate multiple topic models using the same documents in this way.

Please be aware of the following: the idea of generating multiple topic models is that all of them use exactly the same documents. If you add new documents after generating topic models, all the topic models will be erased.

## Visualizing Results
Once you have generated a topic model, you will be able to see several visualizations of that topic model. These are below
- Fill in the three models
![Model settings](./img/topic-models.tab)
![Model overview](./img/topic-models.tab)
![Document list](./img/topic-models.tab)

## Exporting Data
Now we all know that data is nice but it only is as good as you can use it to make a point. Handle has a few different ways of exporting data, both exporting images and exporting raw data.

### Exporting graphics
First, most of the visualizations can be exported as graphics. To do this, look for an “export” button. This will export a PNG file to the file name of your choice.

### Exporting data
Handle also allows exporting a document topics report as a spreadsheet. A document topics report is a table of topic weights for each document.
To do this, select a topic set in the left hand pane of the topic model pane. Then click on (tab name). Then click the export button.
This will export an excel spreadsheet, or (extension) file. This can be explored with excel or libre office, or converted to a comma separated values file. Please note that this is not a comma separated values file. This was done because of the difficulty of importing c a v files into excel for novice users.