/**
 * 1. 데이터를 입력 받는다. ( getInputData )
 * 2. solution 함수로 데이터를 넘긴다. ( solution )
 * 3. solution 함수에서는 데이터를 받아서, map을 통해 각 데이터를 처리한다. ( solution )
 * 4. 각 데이터를 2진수로 변환한다. ( getBinaryNumber )
 * 5. 2진수를 10진수로 변환한다. ( getDecimalNumber )
 * 6. 10진수를 반환한다. ( getDecimalNumber )
 * 7. 반환된 10진수를 배열로 만들어서 반환한다. ( getDecimalNumber )
 * 8. 반환된 데이터의 Max 값을 찾는다. ( getMaximumNumber )
 * 9. Max 값을 반환한다. ( getMaximumNumber )
 * 10. 반환된 Max 값을 출력한다.
 */

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

// // part 2
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
