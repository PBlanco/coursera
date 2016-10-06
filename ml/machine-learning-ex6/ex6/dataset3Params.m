function [C, sigma] = dataset3Params(X, y, Xval, yval)
%EX6PARAMS returns your choice of C and sigma for Part 3 of the exercise
%where you select the optimal (C, sigma) learning parameters to use for SVM
%with RBF kernel
%   [C, sigma] = DATASET3PARAMS(X, y, Xval, yval) returns your choice of C and 
%   sigma. You should complete this function to return the optimal C and 
%   sigma based on a cross-validation set.
%

% You need to return the following variables correctly.
% C = 1;
% sigma = 0.3;

% ====================== YOUR CODE HERE ======================
% Instructions: Fill in this function to return the optimal C and sigma
%               learning parameters found using the cross validation set.
%               You can use svmPredict to predict the labels on the cross
%               validation set. For example, 
%                   predictions = svmPredict(model, Xval);
%               will return the predictions on the cross validation set.
%
%  Note: You can compute the prediction error using 
%        mean(double(predictions ~= yval))
%

step_vec = [0.01 0.03 0.1 0.3 1 3 10 30];
min_err = realmax();

for i = 1:length(step_vec)
    for j = 1:length(step_vec)
        c_step = step_vec(i);
        sigma_step = step_vec(j);
        model = svmTrain(X, y, c_step, @(x1, x2) gaussianKernel(x1, x2, sigma_step)); 
        p = svmPredict(model, Xval);
        err = mean(double(p ~= yval));
        if err < min_err
            C = c_step;
            sigma = sigma_step;
            min_err = err;
    end 
end

% =========================================================================

end
