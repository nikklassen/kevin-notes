'use strict';

// This should be your main point of entry for your app

window.addEventListener('load', function() {
    var modelModule = createModelModule();
    var viewModule = createViewModule();
    var appContainer = document.getElementById('app-container');

    // Attach the file chooser to the page. You can choose to put this elsewhere, and style as desired
    var fileChooser = new viewModule.FileChooser();
    appContainer.appendChild(fileChooser.getElement());

    // Demo that we can choose files and save to local storage. This can be replaced, later
    fileChooser.addListener(function(fileChooser, files, eventDate) {
        var imageCollectionModel = new modelModule.ImageCollectionModel();
        _.each(
            files,
            function(file) {
                var newDiv = document.createElement('div');
                var fileInfo = "File name: " + file.name + ", last modified: " + file.lastModifiedDate;
                newDiv.innerText = fileInfo;
                appContainer.appendChild(newDiv);
                imageCollectionModel.addImageModel(
                    new modelModule.ImageModel(
                        '/images/' + file.name,
                        file.lastModifiedDate,
                        '',
                        0
                    ));
            }
        );
        modelModule.storeImageCollectionModel(imageCollectionModel);
    });
    // Demo retrieval
    var storedImageCollection = modelModule.loadImageCollectionModel();
    var storedImageDiv = document.createElement('div');
    _.each(
        storedImageCollection.getImageModels(),
        function(imageModel) {
            var imageModelDiv = document.createElement('div');
            imageModelDiv.innerText = "ImageModel from storage: " + JSON.stringify(imageModel);
            storedImageDiv.appendChild(imageModelDiv);
        }
    );
    appContainer.appendChild(storedImageDiv);
});