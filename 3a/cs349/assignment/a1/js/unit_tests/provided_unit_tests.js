'use strict';

var expect = chai.expect;
describe('First unit test', function() {
    it('Some tests', function() {
        /*
         We're using Mocha and Chai to do unit testing.

         Mocha is what sets up the tests (the "describe" and "it" portions), while
         Chai does the assertion/expectation checking.

         Links:
         Mocha: http://mochajs.org
         Chai: http://chaijs.com

         Note: This is a bunch of tests in one it; you'll probably want to separate them
         out into separate groups to make debugging easier. It's also more satisfying
         to see a bunch of unit tests pass on the results page :)
        */

        // Here is the most basic test you could think of:
        expect(1==1, '1==1').to.be.ok;

        // You can also for equality:
        expect(1, '1 should equal 1').to.equal(1);

        // JavaScript can be tricky with equality tests
        expect(1=='1', "1 should == '1'").to.be.true;

        // Make sure you understand the differences between == and ===
        expect(1==='1', "1 shouldn't === '1'").to.be.false;

        // Use eql for deep comparisons
        expect([1] == [1], "[1] == [1] should be false because they are different objects").to.be.false;

        expect([1], "[1] eqls [1] should be true").to.eql([1]);
    });

    it('Callback demo unit test', function() {
        /*
        Suppose you have a function or object that accepts a callback function,
        which should be called at some point in time (like, for example, a model
        that will notify listeners when an event occurs). Here's how you can test
        whether the callback is ever called.
         */

        // First, we'll create a function that takes a callback, which the function will
        // later call with a single argument. In tests below, we'll use models that
        // take listeners that will be later called
        var functionThatTakesCallback = function(callbackFn) {
            return function(arg) {
                callbackFn(arg);
            };
        };

        // Now we want to test if the function will ever call the callbackFn when called.
        // To do so, we'll use Sinon's spy capability (http://sinonjs.org/)
        var spyCallbackFn = sinon.spy();

        // Now we'll create the function with the callback
        var instantiatedFn = functionThatTakesCallback(spyCallbackFn);

        // This instantiated function should take a single argument and call the callbackFn with it:
        instantiatedFn("foo");

        // Now we can check that it was called:
        expect(spyCallbackFn.called, 'Callback function should be called').to.be.ok;

        // We can check the number of times called:
        expect(spyCallbackFn.callCount, 'Number of times called').to.equal(1);

        // And we can check that it got its argument correctly:
        expect(spyCallbackFn.calledWith('foo'), 'Argument verification').to.be.true;

        // Or, equivalently, get the first argument of the first call:
        expect(spyCallbackFn.args[0][0], 'Argument verification 2').to.equal('foo');

        // This should help you understand the listener testing code below
    });

    it('Listener unit test for GraphModel', function() {
        var graphModel = new GraphModel();
        var firstListener = sinon.spy();

        graphModel.addListener(firstListener);
        graphModel.selectGraph("MyGraph");

        expect(firstListener.called, 'GraphModel listener should be called').to.be.ok;
        expect(firstListener.args[0][2], 'GraphModel argument verification').to.equal('MyGraph');

        var secondListener = sinon.spy();
        graphModel.addListener(secondListener);
        graphModel.selectGraph("MyGraph");

        expect(firstListener.callCount, 'GraphModel first listener should have been called twice').to.equal(2);
        expect(secondListener.called, "GraphModel second listener should have been called").to.be.ok;
    });
});

describe('Graph Model Unit Tests', function() {
    it('Selects a graph and stores its name', function() {
        var graphModel = new GraphModel();

        graphModel.selectGraph('MyGraph');
        expect(graphModel.getNameOfCurrentlySelectedGraph(), 'GraphModel did not store selected graph name').to.equal('MyGraph');
    });

    it('Handles graph names', function() {
        var graphModel = new GraphModel();

        expect(JSON.stringify(graphModel.getAvailableGraphNames()), 'GraphModel names are not set properly').to.equal(JSON.stringify(['scatter', 'table']));
    });

    it('Adds and removes listeners', function() {
        var graphModel = new GraphModel();
        var firstListener = sinon.spy();
        var secondListener = sinon.spy();
        var thirdListener = sinon.spy();

        graphModel.addListener(firstListener);
        graphModel.addListener(secondListener);
        expect(graphModel.removeListener(thirdListener), 'GraphModel removed non-existent listener').to.equal(-1);

        graphModel.addListener(thirdListener);
        expect(graphModel.listeners.length, 'GraphModel did not add three objects').to.equal(3);

        graphModel.removeListener(thirdListener);
        graphModel.removeListener(firstListener);
        graphModel.removeListener(secondListener);
        expect(graphModel.listeners.length, 'GrapModel did not remove all objects').to.equal(0);
    });
});

describe('Activity Store Model Unit Tests', function() {
    it('Updates activity data point listeners', function() {
        var activityStoreModel = new ActivityStoreModel();
        var listener = sinon.spy();

        activityStoreModel.addListener(listener);
        activityStoreModel.addActivityDataPoint('point');

        expect(listener.called, 'ActivityStoreModel listener should be called').to.be.ok;
        expect(listener.args[0][0], 'ActivityStoreModel argument 0 verification').to.equal(ACTIVITY_DATA_ADDED_EVENT);
        expect(listener.args[0][2], 'ActivityStoreModel argument 2 verification').to.equal('point');
    });

    it('Does not update listeners if the data point does not exist', function() {
        var activityStoreModel = new ActivityStoreModel();
        var listener = sinon.spy();

        activityStoreModel.addListener(listener);
        activityStoreModel.removeActivityDataPoint('point');

        expect(listener.called, 'ActivityStoreModel listener should not be called').to.be.not.ok;
    });
});
