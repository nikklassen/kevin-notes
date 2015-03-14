'use strict';

var expect = chai.expect;

describe('Scene Graph Module', function() {
    it('properly links children', function() {
        var sceneGraphModule = createSceneGraphModule();
        var carNode = new sceneGraphModule.CarNode();
        var carNodeChild = new sceneGraphModule.CarNode();

        carNode.addChild(carNodeChild);

        expect(carNode.parent, 'carNode has invalid parent').to.be.null;
        expect(carNodeChild.parent, 'carNodeChild has invalid parent').to.equal(carNode);

        expect(carNode.children[carNodeChild.nodeName], 'carNode has invalid children').to.equal(carNodeChild);
    });

    it('gets affine transforms', function() {
        var sceneGraphModule = createSceneGraphModule();
        var carNode = new sceneGraphModule.CarNode();

        expect(_.isEqual(carNode.getTransform(), new AffineTransform()), 'default affineTransform is not correct').to.be.true;

        carNode.objectTransform.preRotate(5, 1, 9);

        expect(_.isEqual(carNode.getTransform(), new AffineTransform()), 'modified affineTransform is not correct').to.be.false;
    });

    it('rotates bumpers', function() {
        var sceneGraphModule = createSceneGraphModule();
        var bumperNodeB = new sceneGraphModule.BumperNode(sceneGraphModule.BACK_BUMPER_PART);
        var bumperNodeF = new sceneGraphModule.BumperNode(sceneGraphModule.FRONT_BUMPER_PART);
        var bumperNodeL = new sceneGraphModule.BumperNode(sceneGraphModule.LEFT_BUMPER_PART);

        expect(_.isEqual(bumperNodeF.dimensions, bumperNodeL.dimensions), 'bumpers do not rotate').to.be.false;
        expect(_.isEqual(bumperNodeF.dimensions, bumperNodeB.dimensions), 'bumpers rotate when not required').to.be.true;
    });
});

describe('Buggui', function() {
    it('builds the car', function() {
        var sceneGraphModule = createSceneGraphModule();
        var car1 = buildCar(sceneGraphModule);
        var car2 = buildCar(sceneGraphModule);

        expect(_.isEqual(car1, car2), 'does not create cars deterministically').to.be.true;
    });

    it('performs valid transforms', function() {
        var affine = new AffineTransform();
        affine.preRotate(Math.PI / 2, 0, 0);
        var point = [42, 666];

        var transformed = transform(affine, point, true);

        expect(transformed[0], 'did not transform X properly').to.satisfy(function(num) { return Math.abs(num - point[1]) <= 0.1; });
        expect(transformed[1], 'did not transform Y properly').to.satisfy(function(num) { return Math.abs(num - (-point[0])) <= 0.1; });
    });
});
