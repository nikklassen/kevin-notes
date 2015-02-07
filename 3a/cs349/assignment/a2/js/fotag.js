'use strict';

window.addEventListener('load', function() {
    // Globals
    var modelModule = createModelModule();
    var viewModule = createViewModule();

    var headerContainer = document.getElementById('header-container');
    var chooserContainer = document.getElementById('chooser-container');
    var appContainer = document.getElementById('app-container');


    // Header initialization
    var toolbar = new viewModule.Toolbar();
    headerContainer.appendChild(toolbar.getElement());


    // File Chooser initialization
    var fileChooser = new viewModule.FileChooser();
    chooserContainer.appendChild(fileChooser.getElement());


    // Image Collection View initialization
    var imageCollectionView = new viewModule.ImageCollectionView;
    imageCollectionView.setToView(viewModule.GRID_VIEW);
    imageCollectionView.setImageCollectionModel(modelModule.loadImageCollectionModel());


    // Redraw Image Collection View
    var redrawImages = function() {
        appContainer.removeChild(appContainer.lastChild);
        var renderedImage = imageCollectionView.getElement();
        appContainer.appendChild(renderedImage);
    }

    // redrawImages() removes the last child of appContainer
    appContainer.appendChild(document.createElement('div'));
    redrawImages();


    // Header listener
    toolbar.addListener(function(toolbar, eventType, eventDate) {
        // TODO draw header
        if (eventType == viewModule.GRID_VIEW) {
            imageCollectionView.setToView(viewModule.GRID_VIEW);
            redrawImages();
        } else if (eventType == viewModule.LIST_VIEW) {
            imageCollectionView.setToView(viewModule.LIST_VIEW);
            redrawImages();
        } else if (eventType == viewModule.RATING_CHANGE) {
            // TODO
        }
        while(headerContainer.lastChild) {
            headerContainer.removeChild(headerContainer.lastChild);
        }
        headerContainer.appendChild(toolbar.getElement());
    });


    // New image listener
    fileChooser.addListener(function(fileChooser, files, eventDate) {
        _.each(files, function(file) {
            var imageModel = new modelModule.ImageModel('images/' + file.name, file.lastModifiedDate, file.name, 0)
            imageCollectionView.getImageCollectionModel().addImageModel(imageModel);
        });

        redrawImages();

        modelModule.storeImageCollectionModel(imageCollectionView.getImageCollectionModel());
    });
});
