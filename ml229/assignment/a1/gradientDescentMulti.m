function [theta, J_history] = gradientDescentMulti(X, y, theta, alpha, num_iters)
%GRADIENTDESCENTMULTI Performs gradient descent to learn theta
%   theta = GRADIENTDESCENTMULTI(x, y, theta, alpha, num_iters) updates theta by
%   taking num_iters gradient steps with learning rate alpha

% Initialize some useful values
m = length(y); % number of training examples
J_history = zeros(num_iters, 1);

for iter = 1:num_iters

    % ====================== YOUR CODE HERE ======================
    % Instructions: Perform a single gradient step on the parameter vector
    %               theta.
    %
    % Hint: While debugging, it can be useful to print out the values
    %       of the cost function (computeCostMulti) and gradient here.
    %
    howmany = length(theta);
    tempsum = zeros(1,howmany);
    i = 1;
    while true,
      tempsum(1,i) = sum((X*theta-y)'*X(1:end,i));
      i = i + 1;
      if i > length(tempsum),
	break;
      end;
    end;
    i = 1;
    while true,
      theta(i,1) = theta(i,1) - alpha/m * tempsum(1,i);
      i = i + 1;
      if i > howmany,
	break;
      end;
    end;
    % ============================================================

    % Save the cost J in every iteration
    J_history(iter) = computeCostMulti(X, y, theta);

end

end
