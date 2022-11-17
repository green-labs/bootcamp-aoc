/**
 * 문제 해결 과정은 아래와 같습니다.
 *
 * Part 1
 * 1. 값을 입력 받아 파싱합니다. (getInputData)
 * 2. 파싱한 값을 이진수로 변환합니다. (getBinaryNumber)
 * 3. 이진수를 10진수로 변환합니다. (getDecimalNumber)
 * 4. 10진수 중 최대값을 구합니다. (getMaximumNumber)
 *
 * Part 2
 * 1. 값을 입력 받아 파싱합니다. (getInputData)
 * 2. 파싱한 값을 이진수로 변환합니다. (getBinaryNumber)
 * 3. 이진수를 10진수로 변환합니다. (getDecimalNumber)
 * 4. 내가 찾는 좌석을 찾습니다. (getMissingSeatId)
 */

type getInputDataTypes = () => array<string>

type getBinaryNumberTypes = (string) => string

type getDecimalNumberTypes = (string) => int

type calculateSeatIdTypes = (array<string>) => array<int>

type getMaxValueTypes = (array<int>) => int

type checkSeatIsExistTypes = (array<int>) => (int) => bool

type getMissingSeatIdTypes = (array<int>) => int

/**
 * getInputData
 * @description input 데이터를 파싱합니다.
 * @returns {array<string>}
 *
 * @example
 * getInputData() -> ['FBFBBFFRLR', 'BFFFBBFRRR', 'FFFBBBFRRR', 'BBFFBBFRLL']
 */
let getInputData: getInputDataTypes = () => 
    Node.Fs.readFileAsUtf8Sync("../../../../input/Week1/Year2020Day5.sample.txt") 
    -> Js.String.trim
    |> Js.String.split("\n")


/**
 * getBinaryNumber
 * @description 내가 받은 input을 이진수로 변환
 * @param {string} input
 * @returns {string}
 *
 * @example
 * getBinaryNumber('FBFBBFFRLR') -> '0101100101'
 */
let getBinaryNumber: getBinaryNumberTypes = (input) => 
    input
    -> Js.String2.split("")
    -> Js.Array2.map((x) => 
        switch x {
        | "F" => "0"
        | "B" => "1"
        | "L" => "0"
        | "R" => "1"
        | _ => "0"
        }
    )
    -> Js.Array2.joinWith("")


/**
 * getDecimalNumber
 * @description 이진수를 십진수로 변환
 * @param {string} binary
 * @returns {number}
 *
 * @example
 * getDecimalNumber('0101100101') -> 357
 */
let getDecimalNumber: getDecimalNumberTypes = (binary) =>
    `0b${binary}`
    -> Js.Float.fromString
    -> Belt.Int.fromFloat


/**
 * calculateSeatId
 * @description 조건에 따라 seatId를 계산
 * @param {array<string>} inputs
 * @returns {array<int>}
 *
 * @example
 * calculateSeatId(['FBFBBFFRLR', 'BFFFBBFRRR', 'FFFBBBFRRR', 'BBFFBBFRLL']) -> [357, 567, 119, 820]
 */
let calculateSeatId: calculateSeatIdTypes = (inputs) => 
    inputs
    -> Js.Array2.map((input) =>
        input
        ->getBinaryNumber
        ->getDecimalNumber
    )


/**
 * getMaxValue
 * @description 최대값을 구한다.
 * @param {array<int>} numbers
 * @returns int
 *
 * @example
 * getMaxValue([1, 2, 3, 4, 5]) -> 5
 */
let getMaxValue: getMaxValueTypes = (inputs) => 
    inputs
    ->Js.Math.maxMany_int


let checkSeatIsExist: checkSeatIsExistTypes = (inputs) => (seatId) =>  
    inputs
    ->Js.Array2.includes(seatId)

let getMySeat = (seatId: option<int>) => 
    switch seatId {
    | Some(seatId) => seatId + 1
    | None => 0
    }
    
/**
 * getMissingSeatId
 * @description 빠진 seatId를 구한다.
 * @param {array<int>} seatIds
 * @returns {int}
 *
 * @example
 * getMissingSeatId([119, 121]) -> 120
 *
 * 모든 좌석은 꽉 차있고 빈 좌석은 없기 때문에 내가 찾고자 하는 좌석은 배열에 없는 좌석이다.
 * 따라서, 두 좌석 사이에 기존 배열에 없는 좌석을 찾는것이 문제의 요구사항 이기 때문에 n + 1은 없고 n + 2가 있을 때와 같이 조건을 처리하였다.
 */
let getMissingSeatId = (inputs) => 
    inputs
    ->Js.Array2.find((input) => 
        !checkSeatIsExist(inputs)(input + 1) && 
        checkSeatIsExist(inputs)(input + 2)
    )
    ->getMySeat


let solutionPart1 = () => 
    getInputData()
    ->calculateSeatId
    ->getMaxValue
    ->Js.log


let solutionPart2 = () =>
    getInputData()
    ->calculateSeatId
    ->getMissingSeatId
    ->Js.log


solutionPart1()
solutionPart2()
