'use strict';

window.addEventListener('load', function() {
    // Globals
    var modelModule = createModelModule();
    var viewModule = createViewModule();

    var appContainer = document.getElementById('app-container');


    // File Chooser initialization
    var fileChooser = new viewModule.FileChooser();
    appContainer.appendChild(fileChooser.getElement());


    // Image Collection View initialization
    var imageCollectionView = new viewModule.ImageCollectionView;
    imageCollectionView.setToView(viewModule.GRID_VIEW);
    imageCollectionView.setImageCollectionModel(new modelModule.ImageCollectionModel);
    // imageCollectionView.setImageCollectionModel(modelModule.loadImageCollectionModel());

    var renderedView = imageCollectionView.getElement();
    appContainer.appendChild(renderedView);


    // Redraw Image Collection View
    var redraw = function() {
        appContainer.removeChild(appContainer.lastChild);
        var renderedImage = imageCollectionView.getElement();
        appContainer.appendChild(renderedImage);
    }


    // New image listener
    fileChooser.addListener(function(fileChooser, files, eventDate) {
        _.each(files, function(file) {
            var imageModel = new modelModule.ImageModel('images/' + file.name, file.lastModifiedDate, file.name, 0)
            imageCollectionView.getImageCollectionModel().addImageModel(imageModel);
        });

        redraw();

        // modelModule.storeImageCollectionModel(imageCollectionModel);
    });


    document.getElementById('grid-button').addEventListener('click', function() {
        imageCollectionView.setToView(viewModule.GRID_VIEW);
        redraw();
    });
    document.getElementById('list-button').addEventListener('click', function() {
        imageCollectionView.setToView(viewModule.LIST_VIEW);
        redraw();
    });
});
