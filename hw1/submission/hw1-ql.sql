.header on
.mode column

-- Question 1a: Create a table Edges(Source,Destination) where both Source and Destination are integers.


CREATE TABLE Edges (Source int, Destination int);


-- Question 1b: Insert the tuples (10,5), (6,25), (1,3), (4, 4)


INSERT INTO Edges (Source, Destination)
VALUES (10, 5), (6, 25), (1, 3), (4, 4);

-- Question 1c: Write a SQL statement that returns all tuples. 


SELECT * FROM Edges;


-- Question 1d: Write a SQL statement that returns only column Source for all tuples

SELECT Source FROM Edges;


-- Question 1e: Write a SQL statement that returns all tuples where Source > Destination


SELECT * FROM Edges
WHERE Source > Destination;


-- Question 1f:Now insert the tuple ('-1','2000')
-- answer: this is no problem, because sqlite will automatically convert string into integer.


INSERT INTO Edges (Source, Destination)
VALUES ('-1','2000');
