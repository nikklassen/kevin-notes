'use strict';

/**
 * A function that creates and returns all of the model classes and constants.
 */
function createModelModule() {
    var IMAGE_ADDED_TO_COLLECTION_EVENT = 'IMAGE_ADDED_TO_COLLECTION_EVENT';
    var IMAGE_REMOVED_FROM_COLLECTION_EVENT = 'IMAGE_REMOVED_FROM_COLLECTION_EVENT';
    var IMAGE_META_DATA_CHANGED_EVENT = 'IMAGE_META_DATA_CHANGED_EVENT';

    /**
     * An ImageModel represents a reference to an image on the local file system. You should assume
     * that all images are within the ./images directory.
     * @param pathToFile The relative path to the image. A string.
     * @param modificationDate The modification date of the file. A Date.
     * @param caption A user-supplied caption. Users may not provide a caption. A string.
     * @param rating The rating, from 0-5, the user has provided for the image. The rating is an integer.
     *               A rating of 0 indicates that the user has not yet supplied a rating for the image.
     * @constructor
     */
    var ImageModel = function(pathToFile, modificationDate, caption, rating) {
        if (!_.isString(pathToFile)) {
            throw new Error("Invalid arguments supplied to ImageModel: pathToFile was not a sting. Arguments: " + JSON.stringify(arguments));
        }
        this.path = pathToFile;

        if (!(modificationDate instanceof Date)) {
            throw new Error("Invalid arguments supplied to ImageModel: modificationDate was not a date. Arguments: " + JSON.stringify(arguments));
        }
        this.modificationDate = modificationDate;

        this.setRating(rating);
        this.setCaption(caption);

        this.listeners = [];
    };

    _.extend(ImageModel.prototype, {
        /**
         * Adds a listener to be notified of when the model changes.
         * @param listener A function with the signature: (imageModel, eventTime),
         * where imageModel is a reference to this object, and eventTime is a Date
         * object indicating the time of the event.
         */
        addListener: function(listener) {
            this.listeners.push(listener);
            return this;
        },

        /**
         * Removes the given listener from this object.
         * @param listener
         */
        removeListener: function(listener) {
            if (this.listeners.indexOf(listener) === -1) { return -1; }
            this.listeners = _.filter(this.listeners, function(obj) { return obj != listener; });
            return listener;
        },

        /**
         * Returns a string representing the caption. Must return an empty string if the
         * user has not supplied a caption for the image.
         */
        getCaption: function() {
            return this.caption;
        },

        /**
         * Sets the caption for this image.
         * @param caption A string representing a user caption.
         */
        setCaption: function(caption) {
            if (!_.isString(caption)) {
                throw new Error("Invalid arguments supplied to setCaption. Arguments: " + JSON.stringify(caption));
            }

            this.caption = caption;

            var _this = this;
            _.each(this.listeners, function(obj) {
                obj(_this, (new Date()).getTime());
            });
            return this;
        },

        /**
         * Returns the user-supplied rating of the image. A user can provide a rating between 1-5.
         * If no rating has been given, should return 0.
         */
        getRating: function() {
            return this.rating;
        },

        /**
         * Sets the user-supplied rating of the image.
         * @param rating An integer in the range [0,5] (where a 0 indicates the user is clearing their rating)
         */
        setRating: function(rating) {
            if (!_.isNumber(rating) || rating < 0 || 5 < rating) {
                throw new Error("Invalid arguments supplied to setRating. Arguments: " + JSON.stringify(rating));
            }

            this.rating = rating;

            var _this = this;
            _.each(this.listeners, function(obj) {
                obj(_this, (new Date()).getTime());
            });
            return this;
        },

        /**
         * Returns a complete path to the image suitable for inserting into an img tag.
         */
        getPath: function() {
            return this.path;
        },

        /**
         * Returns the modification date (a Date object) for this image.
         */
        getModificationDate: function() {
            return this.modificationDate;
        }
    });

    /**
     * Manages a collection of ImageModel objects.
     */
    var ImageCollectionModel = function() {
        this.imageModels = [];
        this.listeners = [];
    };

    _.extend(ImageCollectionModel.prototype, {
        /**
         * Adds a listener to the collection to be notified of when the collection or an image
         * in the collection changes.
         * @param listener A function with the signature (eventType, imageModelCollection, imageModel, eventDate),
         *                 where eventType is a string of either
         *                 - IMAGE_ADDED_TO_COLLECTION_EVENT,
         *                 - IMAGE_REMOVED_FROM_COLLECTION_EVENT, or
         *                 - IMAGE_META_DATA_CHANGED_EVENT.
         *                 imageModelCollection is a reference to this object, imageModel is the imageModel
         *                 that was added, removed, or changed, and eventDate is a Date object representing the
         *                 time when the change occurred.
         */
        addListener: function(listener) {
            this.listeners.push(listener);
            return this;
        },

        /**
         * Removes the given listener from the object.
         * @param listener
         */
        removeListener: function(listener) {
            if (this.listeners.indexOf(listener) === -1) { return -1; }
            this.listeners = _.filter(this.listeners, function(obj) { return obj != listener; });
            return listener;
        },

        /**
         * Adds an ImageModel object to the collection. When adding an ImageModel, this object should
         * register as a listener for that object, and notify its own (i.e., the ImageCollectionModel's listeners)
         * when that ImageModel changes.
         * @param imageModel
         */
        addImageModel: function(imageModel) {
            this.imageModels.push(imageModel);

            var _this = this;
            imageModel.addListener(function(imageModelSub, eventTime) {
                _.each(_this.listeners, function(obj) {
                    obj(IMAGE_META_DATA_CHANGED_EVENT, _this, imageModel, eventTime);
                });
            });

            _.each(this.listeners, function(obj) {
                obj(IMAGE_ADDED_TO_COLLECTION_EVENT, _this, imageModel, (new Date()).getTime());
            });
            return this;
        },

        /**
         * Removes the given ImageModel object from the collection and removes any listeners it has
         * registered with the ImageModel.
         * @param imageModel
         */
        removeImageModel: function(imageModel) {
            if (this.imageModels.indexOf(imageModel) === -1) { return -1; }

            var _this = this;
            _.each(_this.imageModels, function(obj) {
                if (obj == imageModel) {
                    obj.removeListener(function(imageModelSub, eventTime) {
                        _.each(_this.listeners, function(obj) {
                            obj(IMAGE_META_DATA_CHANGED_EVENT, _this, imageModel, eventTime);
                        });
                    });
                }
            });
            this.imageModels = _.filter(this.imageModels, function(obj) { return obj != imageModel; });

            _.each(this.listeners, function(obj) {
                obj(IMAGE_REMOVED_FROM_COLLECTION_EVENT, _this, imageModel, (new Date()).getTime());
            });
            return imageModel;
        },

        /**
         * Returns an array of all ImageModel objects currently in this collection.
         */
        getImageModels: function() {
            return this.imageModels.slice();
        }

    });

    /**
     * Given an ImageCollectionModel, stores all of its contents in localStorage.
     */
    function storeImageCollectionModel(imageCollectionModel) {
        var models = _.map(
            imageCollectionModel.getImageModels(),
            function(imageModel) {
                return {
                    path: imageModel.getPath(),
                    modificationDate: imageModel.getModificationDate(),
                    caption: imageModel.getCaption(),
                    rating: imageModel.getRating()
                };
            }
        );
        localStorage.setItem('imageCollectionModel', JSON.stringify(models));
    }

    /**
     * Returns a new ImageCollectionModel object with contents loaded from localStorage.
     */
    function loadImageCollectionModel() {
        var imageCollectionModel = new ImageCollectionModel();
        var modelsJSON = localStorage.getItem('imageCollectionModel');
        if (modelsJSON) {
            var models = JSON.parse(modelsJSON);
            _.each(
                models,
                function(model) {
                    try {
                        var imageModel = new ImageModel(
                            model.path,
                            new Date(model.modificationDate),
                            model.caption,
                            model.rating
                        );
                        imageCollectionModel.addImageModel(imageModel);
                    } catch (err) {
                        console.log("Error creating ImageModel: " + err);
                    }
                }
            );
        }
        return imageCollectionModel;
    }

    // Return an object containing all of our classes and constants
    return {
        ImageModel: ImageModel,
        ImageCollectionModel: ImageCollectionModel,

        IMAGE_ADDED_TO_COLLECTION_EVENT: IMAGE_ADDED_TO_COLLECTION_EVENT,
        IMAGE_REMOVED_FROM_COLLECTION_EVENT: IMAGE_REMOVED_FROM_COLLECTION_EVENT,
        IMAGE_META_DATA_CHANGED_EVENT: IMAGE_META_DATA_CHANGED_EVENT,

        storeImageCollectionModel: storeImageCollectionModel,
        loadImageCollectionModel: loadImageCollectionModel
    };
}
