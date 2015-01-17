'use strict';

var ACTIVITY_DATA_ADDED_EVENT = 'ACTIVITY_DATA_ADDED_EVENT';
var ACTIVITY_DATA_REMOVED_EVENT = 'ACTIVITY_DATA_REMOVED_EVENT';

var GRAPH_SELECTED_EVENT = 'GRAPH_SELECTED_EVENT';

var ActivityData = function(activityType, healthMetricsDict, activityDurationInMinutes) {
    this.activityType = activityType;
    this.activityDataDict = healthMetricsDict;
    this.activityDurationInMinutes = activityDurationInMinutes
};

var ActivityStoreModel = function() {
    this.activityDataPoints = [];
    this.listeners = [];
};

_.extend(ActivityStoreModel.prototype, {
    addListener: function(listener) {
        this.listeners.push(listener);
        return listener;
    },

    removeListener: function(listener) {
        if (this.listeners.indexOf(listener) === -1) { return -1; }
        this.listeners = _.filter(this.listeners, function(obj) { return obj != listener; });
        return listener;
    },

    addActivityDataPoint: function(activityDataPoint) {
        this.activityDataPoints.push(activityDataPoint);
        _.each(this.listeners, function(obj) {
            obj(ACTIVITY_DATA_ADDED_EVENT, (new Date()).getTime(), activityDataPoint);
        });
        return activityDataPoint;
    },

    removeActivityDataPoint: function(activityDataPoint) {
        if (this.activityDataPoints.indexOf(activityDataPoint) === -1) { return -1; }
        this.activityDataPoints = _.filter(self.activityDataPoints, function(obj) {
            return !_.isEqual(obj, activityDataPoint);
        });
        _.each(this.listeners, function(obj) {
            obj(ACTIVITY_DATA_REMOVED_EVENT, (new Date()).getTime(), activityDataPoint);
        });
        return activityDataPoint;
    },

    getActivityDataPoints: function() {
        return this.activityDataPoints;
    }
});

var GraphModel = function() {
    this.graphNames = ['scatter', 'table'];
    this.listeners = [];
    this.selectedGraph = null;
};

_.extend(GraphModel.prototype, {
    addListener: function(listener) {
        this.listeners.push(listener);
        return listener;
    },

    removeListener: function(listener) {
        if (this.listeners.indexOf(listener) === -1) { return -1; }
        this.listeners = _.filter(this.listeners, function(obj) { return obj != listener; });
        return listener;
    },

    getAvailableGraphNames: function() {
        return this.graphNames;
    },

    getNameOfCurrentlySelectedGraph: function() {
        return this.selectedGraph;
    },

    selectGraph: function(graphName) {
        this.selectedGraph = graphName;
        _.each(this.listeners, function(obj) {
            obj(GRAPH_SELECTED_EVENT, (new Date()).getTime(), graphName);
        });
        return graphName;
    }
});
