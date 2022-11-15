const input = require('fs')
  .readFileSync('../../input/Week1/Year2020Day3.sample.txt', 'utf8')
  .toString()
  .trim()
  .split('\n');

// getFootPrint -> array ( 재귀 )
// getTrees -> array
// array -> int

function getFootPrint(input, movements, row, col) {
  const [right, down] = movements;

  if (row >= input.length) {
    return [];
  }

  const footPrint = input[row][col % input[row].length];
  const nextRow = row + down;
  const nextCol = col + right;

  return [footPrint, ...getFootPrint(input, movements, nextRow, nextCol)];
}

function getTrees(footPrints) {
  return footPrints.filter(footPrint => footPrint === '#');
}

function getNumberOfTrees(trees) {
  return trees.length;
}

function getSumOfTrees(trees) {
  return trees.reduce((acc, cur) => acc * cur, 1);
}

function solution(input, movements) {
  const numberOfTrees = movements.map(movement => {
    return getNumberOfTrees(getTrees(getFootPrint(input, movement, 0, 0)));
  });

  return getSumOfTrees(numberOfTrees);
}

const movements = [
  [1, 1],
  [3, 1],
  [5, 1],
  [7, 1],
  [1, 2],
];

console.log(solution(input, movements));
