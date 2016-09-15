function g = sigmoid(z)
%SIGMOID Compute sigmoid functoon
%   J = SIGMOID(z) computes the sigmoid of z.

% You need to return the following variables correctly 
% g = zeros(size(z));

% ====================== YOUR CODE HERE ======================
% Instructions: Compute the sigmoid of each value of z (z can be a matrix,
%               vector or scalar).

% Make a matrix the same size of z, with e values
[m,n] = size(z);
eMatrix = e(m,n);

%Run sigmoid function
g = 1 ./ (1 .+ eMatrix.^-z);


% =============================================================

end
