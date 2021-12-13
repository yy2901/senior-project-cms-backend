Note: This project is not production ready since authentication is not implemented.
# A Lightweight CMS for creatives
This is a content management system that gives a lot of flexibility to web developers for small scale websites. The project is written using Play! Framework, React.js, SqLite, and it is supposed to be deployed to a VPS.
## Usage
### Left Panel, API Editor
Every api endpoint of this CMS is shown in the left panel. A total 2 layers of route is supported. The first layer is Route, the second layer is Entry.
![leftpanel API editor](https://yuhao-personal-storage.s3.us-east-2.amazonaws.com/Screen+Shot+2021-12-12+at+6.10.08+PM.png)
-  Route: can be both a single entry and a collection of entries. When click on the parent route name ('/blog' for example), you can edit the data when regarding parent route as a single entry. ![enter image description here](https://yuhao-personal-storage.s3.us-east-2.amazonaws.com/Screen+Shot+2021-12-12+at+6.17.11+PM.png)
- add/edit template: click and edit the data template for all entries under this route![enter image description here](https://yuhao-personal-storage.s3.us-east-2.amazonaws.com/Screen+Shot+2021-12-12+at+6.18.55+PM.png)
- add entry: type in the title of the entry and slug will be generated, then a new entry will be added under this route
- entry button: entries will be represented by buttons (for example 'Bureau Cool'), on click, you can edit the entry.![enter image description here](https://yuhao-personal-storage.s3.us-east-2.amazonaws.com/Screen+Shot+2021-12-12+at+6.22.25+PM.png)

### GET API EndPoints
|endpoint|data  |
|--|--|
| `/api/{parent}/{entry}` | entry data that includes slug, title, time created, teaser data in JSON, and content data in JSON |
|`/api/{parent}`|parent route data regarding parent route is a single type.|
|`/api/{parent}?pageItems={itemsNumber}&page={pageNumber}&order={DESC\|ASC}`|regarding parent route is a collection type. get the teaser fields of the entries under this parent route according to the page number, by default itemsNumber=5 and order=DESC, means that entries are ordered from new to old and 5 entries will be returned. |
|`/uploads/{file}`|get uploaded file|
|`/api/_fonts`| get the fonts added in the Font Manager|

### Teaser and Content
For secondary entries, there are two sections, Teaser and Content. When calling api for the entry, data in both teaser and content will be returned. When calling the collection api, only teaser from each entry will be returned.

### Flexible Fields Editor
Similar to Strapi.io, Primic.io, WordPress Advanced Custom Field Pro, this CMS let users build and edit flexible fields for JSON response. There are in total 9 build-in field types:
 - text
 - number
 - file (with customizable extension filter)
 - choices (with customizable button style)
 - boolean
 - repeat
 - flexible
 - group
 - WYSIWYG (with customizable toolbar)

Building custom fields in the Template Editor is fast, intuitive, and easy. All data is stored in JSON and can be nested as deep as possible.

### Font Manager
![enter image description here](https://yuhao-personal-storage.s3.us-east-2.amazonaws.com/Screen+Shot+2021-12-12+at+6.49.40+PM.png)
In the Font Manager, you are allowed to upload and name custom fonts. After update, immediately the font will be injected in the CMS UI with the CSS `@font-face` method. You can then use the name defined in the custom HTML and CSS editor. `fontFamily:{fontName}`

### Write HTML/CSS code in Template Editor
- customize html for the choice field
![choice HTML editor](https://yuhao-personal-storage.s3.us-east-2.amazonaws.com/Screen+Shot+2021-12-12+at+9.28.11+PM.png)
- customize HTML for the flexible field![enter image description here](https://yuhao-personal-storage.s3.us-east-2.amazonaws.com/Screen+Shot+2021-12-12+at+9.39.59+PM.png)
- customize CSS for WYSIWYG toolbar, note that all css properties are camel-cased.
![enter image description here](https://yuhao-personal-storage.s3.us-east-2.amazonaws.com/Screen+Shot+2021-12-12+at+10.06.31+PM.png)
These custom codes are injected in the entry editor view. See example below.![enter image description here](https://yuhao-personal-storage.s3.us-east-2.amazonaws.com/Screen+Shot+2021-12-12+at+10.10.19+PM.png)

### File Manager
When uploading file, images will be compresed into different sizes. Videos' cover image will be automatically extracted and thumbnail will be generated. Currently the uploader doesn't let you choose which frame of the video should be the cover image, if you want to do that, pleaes struct an additional file fields in template editor and extract cover images by yourself then upload it.

## Build this project
A pre-built distribution will be posted under Release when this project is production ready.
### Environment:
Node.js 15
Java 11
SBT
### Folder Structure
![enter image description here](https://yuhao-personal-storage.s3.us-east-2.amazonaws.com/Screen+Shot+2021-12-12+at+10.14.02+PM.png)
This repository (Backend), along with the [UI Repo](https://github.com/yy2901/senior-project-cms-ui) should be cloned to under the same folder for storing this project. `cms-data.db` and `uploads` folder will be created at the first time run.
### Start Developing / Building
#### Dev
- go to `senior-project-cms-backend` folder, then run `sbt run`
- open up a new teminal, go to `senior-project-cms-ui` folder, installing dependencies by run `npm i`, then start react dev server by run `npm start`
- Visit app in dev mode on `http://localhost:3000`
Note: Proxy is set to passthrough all backend requests (9000 port) to the frontend (3000 port)
#### Build
- go to `senior-project-cms-ui`, run `npm run build-to-backend-repo`
- after the UI build, go to `senior-project-cms-backend` then run `sbt dist`
