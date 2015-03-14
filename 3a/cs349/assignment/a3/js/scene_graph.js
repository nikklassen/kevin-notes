'use strict';

AffineTransform.prototype.toArray = function() {
    return [this.m00_, this.m10_, this.m01_, this.m11_, this.m02_, this.m12_];
};

function createSceneGraphModule() {
    var GRAPH_NODE = 'GRAPH_NODE';
    var CAR_PART = 'CAR_PART';

    var FRONT_AXLE_PART = 'FRONT_AXLE_PART';
    var BACK_AXLE_PART = 'BACK_AXLE_PART';

    var FRONT_BUMPER_PART = 'FRONT_BUMPER_PART';
    var BACK_BUMPER_PART = 'BACK_BUMPER_PART';
    var LEFT_BUMPER_PART = 'LEFT_BUMPER_PART';
    var RIGHT_BUMPER_PART = 'RIGHT_BUMPER_PART';

    var FRONT_ROOF_PART = 'FRONT_ROOF_PART';
    var BACK_ROOF_PART = 'BACK_ROOF_PART';

    var LEFT_STEERING_PART = 'LEFT_STEERING_PART';
    var RIGHT_STEERING_PART = 'RIGHT_STEERING_PART';

    var FRONT_LEFT_TIRE_PART = 'FRONT_LEFT_TIRE_PART';
    var FRONT_RIGHT_TIRE_PART = 'FRONT_RIGHT_TIRE_PART';
    var BACK_LEFT_TIRE_PART = 'BACK_LEFT_TIRE_PART';
    var BACK_RIGHT_TIRE_PART = 'BACK_RIGHT_TIRE_PART';


    var GraphNode = function(canvas) {
        this.initGraphNode(new AffineTransform(), GRAPH_NODE);
        this.dimensions = [0, 0, canvas.width, canvas.height];
        this.color = '#EEEEEE';
    };

    _.extend(GraphNode.prototype, {
        /**
         * @param startPositionTransform The transform that should be applied prior
         * to performing any rendering, so that the component can render in its own,
         * local, object-centric coordinate system.
         * @param nodeName The name of the node.
         */
        initGraphNode: function(startPositionTransform, nodeName) {
            this.nodeName = nodeName;

            this.startPositionTransform = startPositionTransform;
            this.objectTransform = new AffineTransform();

            this.children = {};
            this.parent = null;

            this.dimensions = [];
            this.maximums = [];

            this.color = '';
        },

        addChild: function(graphNode) {
            this.children[graphNode.nodeName] = graphNode;
            graphNode.parent = this;
        },

        /**
         * Swaps a graph node with a new graph node.
         * @param nodeName The name of the graph node
         * @param newNode The new graph node
         */
        replaceGraphNode: function(nodeName, newNode) {
            if (nodeName in this.children) {
                this.children[nodeName] = newNode;
            } else {
                _.each(
                    _.values(this.children),
                    function(child) {
                        child.replaceGraphNode(nodeName, newNode);
                    }
                );
            }
        },

        /**
         * Render this node and all children using the graphics context provided.
         * @param context
         */
        render: function(context) {
            var transform = this.objectTransform.clone();
            transform.preConcatenate(this.startPositionTransform);

            context.save();
            context.transform.apply(context, transform.toArray());

            context.fillStyle = this.color;
            context.fillRect.apply(context, this.dimensions);

            _.each(
                _.values(this.children),
                function(child) {
                    child.render(context);
                }
            );
            context.restore();
        },

        getTransform: function() {
            var affine = new AffineTransform();

            var ref = this;
            while (ref) {
                affine.preConcatenate(ref.objectTransform);
                affine.preConcatenate(ref.startPositionTransform);
                ref = ref.parent;
            }

            return affine;
        },

        /**
         * Determines whether a point lies within this object.
         */
        pointInObject: function(point) {
            var affine = this.getTransform();
            affine = affine.createInverse();

            var transformed = [];
            affine.transform([point[0], point[1]], 0, transformed, 0, 1);
            return (transformed[0] >= this.dimensions[0] &&
                    transformed[0] <= this.dimensions[0] + this.dimensions[2] &&
                    transformed[1] >= this.dimensions[1] &&
                    transformed[1] <= this.dimensions[1] + this.dimensions[3]);
        },
    });


    var CarNode = function() {
        this.initGraphNode(new AffineTransform(), CAR_PART);
        this.dimensions = [-25, -50, 50, 100];
        this.maximums = [25, 150, 50, 200];
    };

    _.extend(CarNode.prototype, GraphNode.prototype, {
        render: function(context) {
            var transform = this.objectTransform.clone();
            transform.preConcatenate(this.startPositionTransform);

            context.save();
            context.transform.apply(context, transform.toArray());

            _.each(
                _.values(this.children),
                function(child) {
                    if (child.nodeName === FRONT_AXLE_PART || child.nodeName === BACK_AXLE_PART) {
                        child.render(context);
                    }
                }
            );

            context.fillStyle = '#DD0000';
            context.fillRect.apply(context, this.dimensions);

            context.beginPath();
            context.arc(-10, -43, 5, 0, Math.PI * 2 , false);
            context.arc(10, -43, 5, 0, Math.PI * 2, false);
            context.closePath();
            context.fillStyle = '#000000';
            context.fill();

            _.each(
                _.values(this.children),
                function(child) {
                    if (child.nodeName !== FRONT_AXLE_PART && child.nodeName !== BACK_AXLE_PART) {
                        child.render(context);
                    }
                }
            );
            context.restore();
        },
    });


    /**
     * @param axlePartName Which axle this node represents
     * @constructor
     */
    var AxleNode = function(axlePartName) {
        this.initGraphNode(new AffineTransform(), axlePartName);
        this.dimensions = [-25, -2, 50, 4];
        this.maximums = [50, 150, 4, 4];
        this.color = '#000000';
    };

    _.extend(AxleNode.prototype, GraphNode.prototype, {
        pointInObject: function(point) {
            return false;
        },
    });


    /**
     * @param bumperPartName Which bumper this node represents
     * @constructor
     */
    var BumperNode = function(bumperPartName) {
        this.initGraphNode(new AffineTransform(), bumperPartName);
        if (bumperPartName === FRONT_BUMPER_PART || bumperPartName === BACK_BUMPER_PART) {
            this.dimensions = [-25, -2, 50, 4];
        } else {
            this.dimensions = [-2, -50, 4, 100];
        }
        this.color = '#000000';
    };

    _.extend(BumperNode.prototype, GraphNode.prototype, { });


    /**
     * @param roofPartName Which roof this node represents
     * @constructor
     */
    var RoofNode = function(roofPartName) {
        this.initGraphNode(new AffineTransform(), roofPartName);
        this.dimensions = [-15, -10, 30, 20];
        this.color = '#FFFFFF';
    };

    _.extend(RoofNode.prototype, GraphNode.prototype, { });


    /**
     * @param steeringPartName Which steering this node represents
     * @constructor
     */
    var SteeringNode = function(steeringPartName) {
        this.initGraphNode(new AffineTransform(), steeringPartName);
        this.dimensions = [-3, -3, 6, 6];
        this.color = '#000000';
    };

    _.extend(SteeringNode.prototype, GraphNode.prototype, { });


    /**
     * @param tirePartName Which tire this node represents
     * @constructor
     */
    var TireNode = function(tirePartName) {
        this.initGraphNode(new AffineTransform(), tirePartName);
        this.dimensions = [-3, -12, 6, 24];
        this.color = '#000000';
    };

    _.extend(TireNode.prototype, GraphNode.prototype, { });

    return {
        GraphNode: GraphNode,
        CarNode: CarNode,
        AxleNode: AxleNode,
        BumperNode: BumperNode,
        RoofNode: RoofNode,
        SteeringNode: SteeringNode,
        TireNode: TireNode,
        CAR_PART: CAR_PART,
        FRONT_AXLE_PART: FRONT_AXLE_PART,
        BACK_AXLE_PART: BACK_AXLE_PART,
        FRONT_BUMPER_PART: FRONT_BUMPER_PART,
        BACK_BUMPER_PART: BACK_BUMPER_PART,
        LEFT_BUMPER_PART: LEFT_BUMPER_PART,
        RIGHT_BUMPER_PART: RIGHT_BUMPER_PART,
        FRONT_ROOF_PART: FRONT_ROOF_PART,
        BACK_ROOF_PART: BACK_ROOF_PART,
        LEFT_STEERING_PART: LEFT_STEERING_PART,
        RIGHT_STEERING_PART: RIGHT_STEERING_PART,
        FRONT_LEFT_TIRE_PART: FRONT_LEFT_TIRE_PART,
        FRONT_RIGHT_TIRE_PART: FRONT_RIGHT_TIRE_PART,
        BACK_LEFT_TIRE_PART: BACK_LEFT_TIRE_PART,
        BACK_RIGHT_TIRE_PART: BACK_RIGHT_TIRE_PART
    };
}
