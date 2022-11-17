const getInputData = () =>
  require('fs')
    .readFileSync('../../input/Week1/Year2020Day5.sample.txt', 'utf8')
    .toString()
    .trim()
    .split('\n');

const getBinaryNumber = input => {
  const dataSet = {
    F: '0',
    B: '1',
    L: '0',
    R: '1',
  };

  return input
    .split('')
    .map(data => dataSet[data])
    .join('');
};

const getDecimalNumber = binary => parseInt(binary, 2);

const getMaximumNumber = numbers => Math.max(...numbers);

const calculateSeatId = inputs => {
  return inputs.map(input => {
    const binary = getBinaryNumber(input);
    const decimal = getDecimalNumber(binary);

    return decimal;
  });
};

// part 2
const getMissingSeatId = seatIds => {
  return (
    seatIds.find(seatId => {
      return !seatIds.includes(seatId + 1) && seatIds.includes(seatId + 2);
    }) + 1
  );
};

const solution = inputs => {
  const seatIds = calculateSeatId(inputs);
  return getMissingSeatId(seatIds);
};

const input = getInputData();
const result = solution(input);

console.log(result);
