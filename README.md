# Apriori_Algorithm
Apriori Algorithm to find frequent itemsets and generate association rules.

Given the dataset gene_expression.txt. Whose first column denotes the sample id, 
and the last column is disease name. For the rest columns, each column represents one gene.

Implemented the Apriori algorithm to find all frequent itemsets. Reported the number of all the
frequent itemsets generated for support values 30%, 40%, 50%, 60%, and 70%, respectively.

Generated association rules that satisfy following templates. Test templates:
Template 1:
{RULE|BODY|HEAD} HAS ({ANY|NUMBER|NONE}) OF (ITEM1, ITEM2, ..., ITEMn)
Template 2: SizeOf({BODY|HEAD|RULE}) ≥ NUMBER.
Template 3: Any combined templates using AND or OR. For example:
HEAD HAS (1) OF (Disease) AND BODY HAS (NONE) OF (Disease)
