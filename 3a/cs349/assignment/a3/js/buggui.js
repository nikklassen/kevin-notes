'use strict';

window.addEventListener('load', function() {
    var sceneGraphModule = createSceneGraphModule();
    var appContainer = document.getElementById('app-container');

    var canvas = document.getElementById('car_canvas');
    canvas.width = 800;
    canvas.height = 600;

    var graphNode = new sceneGraphModule.GraphNode(canvas);

    var car = buildCar(sceneGraphModule);
    graphNode.addChild(car.carNode);

    var ctx = canvas.getContext('2d');
    graphNode.render(ctx);

    createEventListeners(canvas, ctx, car, graphNode);
});

function createEventListeners(canvas, ctx, car, graphNode) {
    var bumperHeightMouseDown = false;
    var bumperWidthMouseDown = false;
    var carMouseDown = false;
    var roofMouseDown = false;
    var steeringMouseDown = false;
    var tireMouseDown = false;

    var axleDown = null;
    var tireDown = null;

    canvas.addEventListener('mousedown', function(e) {
        if (car.steeringNodeLeft.pointInObject([e.offsetX, e.offsetY])) {
            steeringMouseDown = true;
            tireDown = car.tireNodeFrontLeft;
        } else if (car.steeringNodeRight.pointInObject([e.offsetX, e.offsetY])) {
            steeringMouseDown = true;
            tireDown = car.tireNodeFrontRight;
        } else if (car.tireNodeFrontLeft.pointInObject([e.offsetX, e.offsetY]) ||
                car.tireNodeFrontRight.pointInObject([e.offsetX, e.offsetY])) {
            tireMouseDown = true;
            axleDown = car.axleNodeFront;
        } else if (car.tireNodeBackLeft.pointInObject([e.offsetX, e.offsetY]) ||
                car.tireNodeBackRight.pointInObject([e.offsetX, e.offsetY])) {
            tireMouseDown = true;
            axleDown = car.axleNodeBack;
        } else if (car.roofNodeFront.pointInObject([e.offsetX, e.offsetY]) ||
                car.roofNodeBack.pointInObject([e.offsetX, e.offsetY])) {
            roofMouseDown = true;
        } else if (car.bumperNodeFront.pointInObject([e.offsetX, e.offsetY]) ||
                car.bumperNodeBack.pointInObject([e.offsetX, e.offsetY])) {
            bumperHeightMouseDown = true;
        } else if (car.bumperNodeLeft.pointInObject([e.offsetX, e.offsetY]) ||
                car.bumperNodeRight.pointInObject([e.offsetX, e.offsetY])) {
            bumperWidthMouseDown = true;
        } else if (car.carNode.pointInObject([e.offsetX, e.offsetY])) {
            carMouseDown = true;
        }
    });

    canvas.addEventListener('mousemove', function(e) {
        if (car.steeringNodeLeft.pointInObject([e.offsetX, e.offsetY]) ||
                car.steeringNodeRight.pointInObject([e.offsetX, e.offsetY])) {
            this.style.cursor = 'url(img/rotate.png), auto';
        } else if (car.tireNodeFrontLeft.pointInObject([e.offsetX, e.offsetY])) {
            this.style.cursor = 'w-resize';
        } else if (car.tireNodeFrontRight.pointInObject([e.offsetX, e.offsetY])) {
            this.style.cursor = 'e-resize';
        } else if (car.tireNodeBackLeft.pointInObject([e.offsetX, e.offsetY])) {
            this.style.cursor = 'w-resize';
        } else if (car.tireNodeBackRight.pointInObject([e.offsetX, e.offsetY])) {
            this.style.cursor = 'e-resize';
        } else if (car.roofNodeFront.pointInObject([e.offsetX, e.offsetY]) ||
                car.roofNodeBack.pointInObject([e.offsetX, e.offsetY])) {
            this.style.cursor = 'url(img/rotate.png), auto';
        } else if (car.bumperNodeFront.pointInObject([e.offsetX, e.offsetY])) {
            this.style.cursor = 'ns-resize';
        } else if (car.bumperNodeBack.pointInObject([e.offsetX, e.offsetY])) {
            this.style.cursor = 'ns-resize';
        } else if (car.bumperNodeLeft.pointInObject([e.offsetX, e.offsetY])) {
            this.style.cursor = 'ew-resize';
        } else if (car.bumperNodeRight.pointInObject([e.offsetX, e.offsetY])) {
            this.style.cursor = 'ew-resize';
        } else if (car.carNode.pointInObject([e.offsetX, e.offsetY])) {
            this.style.cursor = 'move';
        } else {
            this.style.cursor = 'default';
        }
    });

    canvas.addEventListener('mouseup', function(_e) {
        bumperHeightMouseDown = false;
        bumperWidthMouseDown = false;
        carMouseDown = false;
        roofMouseDown = false;
        steeringMouseDown = false;
        tireMouseDown = false;

        axleDown = null;
        tireDown = null;
    });

    var carNodeListener = function(e) {
        if (carMouseDown) {
            var transformed = transform(car.carNode.objectTransform, [e.offsetX, e.offsetY], true);

            car.carNode.objectTransform.translate(transformed[0], transformed[1]);
            graphNode.render(ctx);
        }
    };
    canvas.addEventListener('mousemove', carNodeListener);

    var bumperNodeListener = function(e) {
        if (bumperHeightMouseDown) {
            var original = car.carNode.objectTransform.clone();
            var transformed = transform(car.carNode.objectTransform, [e.offsetX, e.offsetY], true);

            car.carNode.objectTransform.scale(1, Math.abs(transformed[1]) * 2 / car.carNode.dimensions[3]);

            if (!legalSize(car.carNode, true)) {
                car.carNode.objectTransform.copyFrom(original);
            }

            graphNode.render(ctx);
        } else if (bumperWidthMouseDown) {
            var original = car.carNode.objectTransform.clone();
            var transformed = transform(car.carNode.objectTransform, [e.offsetX, e.offsetY], true);

            car.carNode.objectTransform.scale(Math.abs(transformed[0]) * 2 / car.carNode.dimensions[2], 1);

            if (!legalSize(car.carNode, false)) {
                car.carNode.objectTransform.copyFrom(original);
            }

            graphNode.render(ctx);
        }
    };
    canvas.addEventListener('mousemove', bumperNodeListener);

    var roofNodeListener = function(e) {
        if (roofMouseDown) {
            var original = transform(car.carNode.objectTransform, [0, 0], false);
            var transformed = transform(car.carNode.objectTransform, [e.offsetX, e.offsetY], true);

            if (transformed[1] > 0) {
                transformed[0] *= -1;
                transformed[1] *= -1;
            }

            var angle = Math.atan2(transformed[0], Math.abs(transformed[1]));
            car.carNode.objectTransform.preRotate(angle, original[0], original[1]);
            graphNode.render(ctx);
        }
    };
    canvas.addEventListener('mousemove', roofNodeListener);

    var steeringNodeListener = function(e) {
        if (steeringMouseDown) {
            var original = tireDown.objectTransform.clone();
            var transformed = transform(tireDown.getTransform(), [e.offsetX, e.offsetY], true);

            if (transformed[1] > 0) {
                transformed[0] *= -1;
                transformed[1] *= -1;
            }

            var angle = Math.atan2(transformed[0], Math.abs(transformed[1]));
            original.preRotate(angle, 0, 0);

            if (Math.acos(original.getScaleX()) < Math.PI / 4) {
                car.tireNodeFrontLeft.objectTransform.copyFrom(original);
                car.tireNodeFrontRight.objectTransform.copyFrom(original);
                graphNode.render(ctx);
            }
        }
    }
    canvas.addEventListener('mousemove', steeringNodeListener);

    var tireNodeListener = function(e) {
        if (tireMouseDown) {
            var original = axleDown.objectTransform.clone();
            var transformed = transform(axleDown.getTransform(), [e.offsetX, e.offsetY], true);

            car.axleNodeFront.objectTransform.scale(Math.abs(transformed[0] / axleDown.dimensions[0]), 1);
            car.axleNodeBack.objectTransform.scale(Math.abs(transformed[0] / axleDown.dimensions[0]), 1);

            if (!legalSize(car.axleNodeFront, false)) {
                car.axleNodeFront.objectTransform.copyFrom(original);
                car.axleNodeBack.objectTransform.copyFrom(original);
            }

            graphNode.render(ctx);
        }
    };
    canvas.addEventListener('mousemove', tireNodeListener);
}

function transform(objTransform, point, invert) {
    var lp = [];
    if (invert) {
        objTransform = objTransform.createInverse();
    }
    objTransform.transform([point[0], point[1]], 0, lp, 0, 1);
    return lp;
}

function legalSize(node, checkHeight) {
    function distance(points) {
        var x1 = points[0];
        var y1 = points[1];
        var x2 = points[2];
        var y2 = points[3];

        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }

    var position = node.startPositionTransform.clone();
    position.concatenate(node.objectTransform);

    var transformed = [];
    if (checkHeight) {
        position.transform([0, node.dimensions[1], 0, -node.dimensions[1]], 0, transformed, 0, 2);
        var dis = distance(transformed);
        return node.maximums[2] <= dis && dis <= node.maximums[3];
    } else {
        position.transform([node.dimensions[0], 0, -node.dimensions[0], 0], 0, transformed, 0, 2);
        var dis = distance(transformed);
        return node.maximums[0] <= dis && dis <= node.maximums[1];
    }
}

function buildCar(sceneGraphModule) {
    var carNode = new sceneGraphModule.CarNode();
    carNode.objectTransform.translate(400, 300);

    // Bumpers
    var bumperNodeFront = new sceneGraphModule.BumperNode(sceneGraphModule.FRONT_BUMPER_PART);
    bumperNodeFront.startPositionTransform.translate(0, -48);
    carNode.addChild(bumperNodeFront);

    var bumperNodeBack = new sceneGraphModule.BumperNode(sceneGraphModule.BACK_BUMPER_PART);
    bumperNodeBack.startPositionTransform.translate(0, 48);
    carNode.addChild(bumperNodeBack);

    var bumperNodeLeft = new sceneGraphModule.BumperNode(sceneGraphModule.LEFT_BUMPER_PART);
    bumperNodeLeft.startPositionTransform.translate(-24, 0);
    carNode.addChild(bumperNodeLeft);

    var bumperNodeRight = new sceneGraphModule.BumperNode(sceneGraphModule.RIGHT_BUMPER_PART);
    bumperNodeRight.startPositionTransform.translate(24, 0);
    carNode.addChild(bumperNodeRight);

    // Roofs
    var roofNodeFront = new sceneGraphModule.RoofNode(sceneGraphModule.FRONT_ROOF_PART);
    roofNodeFront.startPositionTransform.translate(0, -20);
    carNode.addChild(roofNodeFront);

    var roofNodeBack = new sceneGraphModule.RoofNode(sceneGraphModule.BACK_ROOF_PART);
    roofNodeBack.startPositionTransform.translate(0, 20);
    carNode.addChild(roofNodeBack);

    // Axles
    var axleNodeFront = new sceneGraphModule.AxleNode(sceneGraphModule.FRONT_AXLE_PART);
    axleNodeFront.startPositionTransform.translate(0, -22);
    carNode.addChild(axleNodeFront);

    var axleNodeBack = new sceneGraphModule.AxleNode(sceneGraphModule.BACK_AXLE_PART);
    axleNodeBack.startPositionTransform.translate(0, 22);
    carNode.addChild(axleNodeBack);

    // Tires
    var tireNodeFrontLeft = new sceneGraphModule.TireNode(sceneGraphModule.FRONT_LEFT_TIRE_PART);
    tireNodeFrontLeft.startPositionTransform.translate(-27, 0);
    axleNodeFront.addChild(tireNodeFrontLeft);

    var tireNodeFrontRight = new sceneGraphModule.TireNode(sceneGraphModule.FRONT_RIGHT_TIRE_PART);
    tireNodeFrontRight.startPositionTransform.translate(27, 0);
    axleNodeFront.addChild(tireNodeFrontRight);

    var tireNodeBackLeft = new sceneGraphModule.TireNode(sceneGraphModule.BACK_LEFT_TIRE_PART);
    tireNodeBackLeft.startPositionTransform.translate(-27, 0);
    axleNodeBack.addChild(tireNodeBackLeft);

    var tireNodeBackRight = new sceneGraphModule.TireNode(sceneGraphModule.BACK_RIGHT_TIRE_PART);
    tireNodeBackRight.startPositionTransform.translate(27, 0);
    axleNodeBack.addChild(tireNodeBackRight);

    // Steering
    var steeringNodeLeft = new sceneGraphModule.SteeringNode(sceneGraphModule.LEFT_STEERING_PART);
    steeringNodeLeft.startPositionTransform.translate(0, -9);
    tireNodeFrontLeft.addChild(steeringNodeLeft);

    var steeringNodeRight = new sceneGraphModule.SteeringNode(sceneGraphModule.RIGHT_STEERING_PART);
    steeringNodeRight.startPositionTransform.translate(0, -9);
    tireNodeFrontRight.addChild(steeringNodeRight);

    return {
        carNode: carNode,
        axleNodeFront: axleNodeFront,
        axleNodeBack: axleNodeBack,
        bumperNodeFront: bumperNodeFront,
        bumperNodeBack: bumperNodeBack,
        bumperNodeLeft: bumperNodeLeft,
        bumperNodeRight: bumperNodeRight,
        roofNodeFront: roofNodeFront,
        roofNodeBack: roofNodeBack,
        steeringNodeLeft: steeringNodeLeft,
        steeringNodeRight: steeringNodeRight,
        tireNodeFrontLeft: tireNodeFrontLeft,
        tireNodeFrontRight: tireNodeFrontRight,
        tireNodeBackLeft: tireNodeBackLeft,
        tireNodeBackRight: tireNodeBackRight
    };
}
