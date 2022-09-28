const fs = require('fs');
const input = fs.readFileSync('../../input/Week1/Year2020Day3.txt', 'utf-8');

{
  const coord = { x: 0, trees: 0 };

  input.trim().split('\n').forEach((row, index) => {
    if (index > 0) {
      coord.x += 3;

      if (row[coord.x % row.length] === '#') {
        coord.trees++;
      }
    }
  });

  console.log(coord.trees);
}

{
  const trees = input.trim().split('\n').reduce((numTrees, row, index) => {
    if (index === 0) return 0;
    const x = index * 3;
    if (row[x % row.length] === '#') numTrees++;
    return numTrees;
  }, 0);

  console.log(trees);
}
