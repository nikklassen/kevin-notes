'use strict';

/**
 * A function that creates and returns all of the model classes and constants.
  */
function createViewModule() {
    var LIST_VIEW = 'LIST_VIEW';
    var GRID_VIEW = 'GRID_VIEW';
    var RATING_CHANGE = 'RATING_CHANGE';

    /**
     * An object representing a DOM element that will render the given ImageModel object.
     */
    var ImageRenderer = function(imageModel, viewType) {
        this.imageModel = imageModel;
        this.viewType = viewType;
    };

    _.extend(ImageRenderer.prototype, {
        /**
         * Returns an element representing the ImageModel, which can be attached to the DOM
         * to display the ImageModel.
         */
        getElement: function() {
            var _this = this;

            var templateDiv = document.createElement('div');
            if (this.viewType == GRID_VIEW) {
                templateDiv.className = 'grid-image';
            } else if (this.viewType == LIST_VIEW) {
                templateDiv.className = 'list-image';
            }

            var template = document.getElementById('img-template').content.cloneNode(true);
            template.querySelector('img').src = this.imageModel.getPath();
            template.querySelector('#img-caption').innerText = this.imageModel.getCaption();

            var date = this.imageModel.getModificationDate();
            template.querySelector('#img-date').innerText = date.getFullYear() + '-' + date.getMonth() + '-' + date.getDay();
            var ratingTemplate = template.querySelector('#rating');
            ratingTemplate.value = this.imageModel.getRating();
            ratingTemplate.addEventListener('input', function() {
                _this.imageModel.setRating(parseInt(ratingTemplate.value));
            });

            templateDiv.appendChild(template);
            return templateDiv;
        },

        /**
         * Returns the ImageModel represented by this ImageRenderer.
         */
        getImageModel: function() {
            return this.imageModel;
        },

        /**
         * Sets the ImageModel represented by this ImageRenderer, changing the element and its
         * contents as necessary.
         */
        setImageModel: function(imageModel) {
            this.imageModel = imageModel;
            return this;
        },

        /**
         * Returns a string of either LIST_VIEW or GRID_VIEW indicating which view type it is
         * currently rendering.
         */
        getCurrentView: function() {
            return this.viewType;
        },

        /**
         * Changes the rendering of the ImageModel to either list or grid view.
         * @param viewType A string, either LIST_VIEW or GRID_VIEW
         */
        setToView: function(viewType) {
            this.viewType = viewType;
            return this;
        }
    });

    /**
     * A factory is an object that creates other objects. In this case, this object will create
     * objects that fulfill the ImageRenderer class's contract defined above.
     */
    var ImageRendererFactory = function() {
        this.imageRenderer = ImageRenderer;
    };

    _.extend(ImageRendererFactory.prototype, {
        /**
         * Creates a new ImageRenderer object for the given ImageModel
         */
        createImageRenderer: function(imageModel, viewType) {
            return new this.imageRenderer(imageModel, viewType);
        }
    });

    /**
     * An object representing a DOM element that will render an ImageCollectionModel.
     * Multiple such objects can be created and added to the DOM (i.e., you shouldn't
     * assume there is only one ImageCollectionView that will ever be created).
     */
    var ImageCollectionView = function() {
        this.imageRendererFactory = new ImageRendererFactory;
        this.imageCollectionModel = null;
        this.viewType = null;

        this.renders = [];
    };

    _.extend(ImageCollectionView.prototype, {
        /**
         * Returns an element that can be attached to the DOM to display the ImageCollectionModel
         * this object represents.
         */
        getElement: function() {
            var _this = this;

            var imageCollection = document.createElement('div');
            _.each(this.renders, function(imageRender) {
                imageCollection.appendChild(imageRender.getElement());
            });
            return imageCollection;
        },

        /**
         * Re-renders all current imageModels
         */
        render: function() {
            if (this.imageRendererFactory === null || this.imageCollectionModel === null || this.viewType === null) {
                return -1;
            }

            this.renders = [];

            var _this = this;
            _.each(this.imageCollectionModel.getImageModels(), function(imageModel) {
                _this.renders.push(_this.imageRendererFactory.createImageRenderer(imageModel, _this.viewType));
            });
        },

        /**
         * Filters renders by rating
         */
        filter: function(rating) {
            this.render();
            this.renders = _.filter(this.renders, function(obj) { return obj.imageModel.getRating() >= rating; });
        },

        /**
         * Gets the current ImageRendererFactory being used to create new ImageRenderer objects.
         */
        getImageRendererFactory: function() {
            return this.imageRendererFactory;
        },

        /**
         * Sets the ImageRendererFactory to use to render ImageModels. When a *new* factory is provided,
         * the ImageCollectionView should redo its entire presentation, replacing all of the old
         * ImageRenderer objects with new ImageRenderer objects produced by the factory.
         */
        setImageRendererFactory: function(imageRendererFactory) {
            this.imageRendererFactory = imageRendererFactory;

            this.render();
            return this;
        },

        /**
         * Returns the ImageCollectionModel represented by this view.
         */
        getImageCollectionModel: function() {
            return this.imageCollectionModel;
        },

        /**
         * Sets the ImageCollectionModel to be represented by this view. When setting the ImageCollectionModel,
         * you should properly register/unregister listeners with the model, so you will be notified of
         * any changes to the given model.
         */
        setImageCollectionModel: function(imageCollectionModel) {
            var _this = this;
            if (this.imageCollectionModel !== null) {
                this.imageCollectionModel.removeListener(function(eventType, imageCollectionModel, imageModel, eventTime) {
                    _this.render();
                });
            }
            this.imageCollectionModel = imageCollectionModel;
            this.imageCollectionModel.addListener(function(eventType, imageCollectionModel, imageModel, eventTime) {
                _this.render();
            });

            this.render();
            return this;
        },

        /**
         * Returns a string of either LIST_VIEW or GRID_VIEW indicating which view type is currently
         * being rendered.
         */
        getCurrentView: function() {
            return this.viewType;
        },

        /**
         * Changes the presentation of the images to either grid view or list view.
         * @param viewType A string of either LIST_VIEW or GRID_VIEW.
         */
        setToView: function(viewType) {
            this.viewType = viewType;

            this.render();
            return this;
        }
    });

    /**
     * An object representing a DOM element that will render the toolbar to the screen.
     */
    var Toolbar = function() {
        this.viewType = GRID_VIEW;
        this.ratingFilter = 0;
        this.listeners = [];
    };

    _.extend(Toolbar.prototype, {
        /**
         * Returns an element representing the toolbar, which can be attached to the DOM.
         */
        getElement: function() {
            var template = document.getElementById('header-template').content.cloneNode(true);
            var gridButton = template.querySelector('#grid-button');
            var listButton = template.querySelector('#list-button');

            if (this.viewType == GRID_VIEW) {
                gridButton.className += 'selected';
                listButton.className -= 'selected';
            } else if (this.viewType == LIST_VIEW) {
                listButton.className += 'selected';
                gridButton.className -= 'selected';
            }

            var _this = this;
            gridButton.addEventListener('click', function() {
                _this.setToView(GRID_VIEW);
            });
            listButton.addEventListener('click', function() {
                _this.setToView(LIST_VIEW);
            });

            var ratingFilter = template.querySelector('#rating-filter');
            ratingFilter.value = this.ratingFilter;
            ratingFilter.addEventListener('input', function() {
                _this.setRatingFilter(parseInt(ratingFilter.value));
            });

            return template;
        },

        /**
         * Registers the given listener to be notified when the toolbar changes from one
         * view type to another.
         * @param listener A function with signature (toolbar, eventType, eventDate), where
         *                 toolbar is a reference to this object, eventType is a string of
         *                 either, LIST_VIEW, GRID_VIEW, or RATING_CHANGE representing how
         *                 the toolbar has changed (specifically, the user has switched to
         *                 a list view, grid view, or changed the star rating filter).
         *                 eventDate is a Date object representing when the event occurred.
         */
        addListener: function(listener) {
            this.listeners.push(listener);
            return this;
        },

        /**
         * Removes the given listener from the toolbar.
         */
        removeListener: function(listener) {
            if (this.listeners.indexOf(listener) === -1) { return -1; }
            this.listeners = _.filter(this.listeners, function(obj) { return obj != listener; });
            return listener;
        },
        /**
         * Returns the current view selected in the toolbar, a string that is
         * either LIST_VIEW or GRID_VIEW.
         */
        getCurrentView: function() {
            return this.viewType;
        },

        /**
         * Sets the toolbar to either grid view or list view.
         * @param viewType A string of either LIST_VIEW or GRID_VIEW representing the desired view.
         */
        setToView: function(viewType) {
            this.viewType = viewType;

            var _this = this;
            _.each(this.listeners, function(obj) {
                obj(_this, viewType, (new Date()).getTime());
            });

            return this;
        },


        /**
         * Returns the current rating filter. A number in the range [0,5], where 0 indicates no
         * filtering should take place.
         */
        getCurrentRatingFilter: function() {
            return this.ratingFilter;
        },

        /**
         * Sets the rating filter.
         * @param rating An integer in the range [0,5], where 0 indicates no filtering should take place.
         */
        setRatingFilter: function(rating) {
            this.ratingFilter = rating;

            var _this = this;
            _.each(this.listeners, function(obj) {
                obj(_this, RATING_CHANGE, (new Date()).getTime());
            });

            return this;
        }
    });

    /**
     * An object that will allow the user to choose images to display.
     * @constructor
     */
    var FileChooser = function() {
        this.listeners = [];
        this._init();
    };

    _.extend(FileChooser.prototype, {
        // This code partially derived from: http://www.html5rocks.com/en/tutorials/file/dndfiles/
        _init: function() {
            var self = this;
            this.fileChooserDiv = document.createElement('div');
            var fileChooserTemplate = document.getElementById('file-chooser');
            this.fileChooserDiv.appendChild(document.importNode(fileChooserTemplate.content, true));
            var fileChooserInput = this.fileChooserDiv.querySelector('.files-input');
            fileChooserInput.addEventListener('change', function(evt) {
                var files = evt.target.files;
                var eventDate = new Date();
                _.each(
                    self.listeners,
                    function(listener_fn) {
                        listener_fn(self, files, eventDate);
                    }
                );
            });
        },

        /**
         * Returns an element that can be added to the DOM to display the file chooser.
         */
        getElement: function() {
            return this.fileChooserDiv;
        },

        /**
         * Adds a listener to be notified when a new set of files have been chosen.
         * @param listener_fn A function with signature (fileChooser, fileList, eventDate), where
         *                    fileChooser is a reference to this object, fileList is a list of files
         *                    as returned by the File API, and eventDate is when the files were chosen.
         */
        addListener: function(listener_fn) {
            if (!_.isFunction(listener_fn)) {
                throw new Error("Invalid arguments to FileChooser.addListener: " + JSON.stringify(arguments));
            }

            this.listeners.push(listener_fn);
        },

        /**
         * Removes the given listener from this object.
         * @param listener_fn
         */
        removeListener: function(listener_fn) {
            if (!_.isFunction(listener_fn)) {
                throw new Error("Invalid arguments to FileChooser.removeListener: " + JSON.stringify(arguments));
            }

            this.listeners = _.without(this.listeners, listener_fn);
        }
    });

    // Return an object containing all of our classes and constants
    return {
        ImageRenderer: ImageRenderer,
        ImageRendererFactory: ImageRendererFactory,
        ImageCollectionView: ImageCollectionView,
        Toolbar: Toolbar,
        FileChooser: FileChooser,

        LIST_VIEW: LIST_VIEW,
        GRID_VIEW: GRID_VIEW,
        RATING_CHANGE: RATING_CHANGE
    };
}
