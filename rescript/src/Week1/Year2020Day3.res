/**
* 문제 해결 단계는 다음과 같습니다.
* 1. 먼저, 데이터를 사용할 수 있게 파싱하자 (parseInput)
* 2. 그 다음, 내가 어디에 방문했는지를 알아내자 (getFootPrint)
* 3. 내가 알고자 하는 것은 "나무"이니, 나무가 아닌 것들을 Filter 하자 (getTrees)
* 4. 나무의 갯수를 알아내자 (getNumberOfTrees)
*/

type movement = {
    right: int,
    down: int
}

type args = {
    input: array<string>,
    movement: movement,
    row: int,
    col: int,
}

type rec getFootPrintTypes = (args, array<string>) => array<string>

type soltionTypes = (array<string>, array<movement>) => int


/**
* parseInput
* @description input을 사용할 수 있게 파싱하는 작업 수행.
* @returns {array<string>}
* 
* input   output
* *.*    
* ..* => ["*.*", "..*", ".*."]
* .*.
*/
let parseInput = () => {
    Node.Fs.readFileAsUtf8Sync("../../../../input/Week1/Year2020Day3.sample.txt") 
        -> Js.String.trim
        |> Js.String.split("\n")
}

/**
* getFootPrint
* @description 내가 방문한 곳의 값을 반환하는 함수
* @param {args} args
* @param {array<string>} value
* @returns {array<string>}
*
* + 매개변수 레코드로 리펙터링
* + 일반적인 재귀가 아닌 꼬리 재귀로 변경 ( Tail Call Optimization )
*/
let rec getFootPrint: getFootPrintTypes = (args, value) => {

    let {input, movement, row, col} = args;

    // 재귀 호출이 끝나면 아무 일도 하지 않고 결과만 바로 반환되도록 하는 방법 (TOC)
    switch (row >= input->Array.length) {
    | true => value
    | false => {
        let footPrint = Js.String.get(input[row], mod(col, Js.String.length(input[row])));
        let nextRow = row + movement.down;
        let nextCol = col + movement.right;
        let nextValue = Js.Array.concat([footPrint], value);

        getFootPrint({
            ...args,
            row: nextRow,
            col: nextCol
        }, nextValue);
    }}
}

/**
* getTrees
* @description 내가 방문한 곳의 나무만 Filter 하는 함수
* @param {array<string>} footPrints
* @returns {array<string>}
*/
let getTrees = (footPrints: array<string>) => {
    footPrints
    |> Js.Array.filter(footPrint => footPrint == "#")
} 

/**
* getNumberOfTrees
* @description 내가 방문한 곳의 나무의 갯수를 반환하는 함수
* @param {array<string>} trees
* @returns {int}
*/
let getNumberOfTrees = (trees: array<string>) => Js.Array.length(trees)

/**
* getMultiplyCountOfTrees
* @description 내가 방문한 곳의 나무의 곱을 계산하는 함수
* @param {array<string>} trees
* @returns {int}
*/
let getMultiplyCountOfTrees = (trees: array<int>) => Js.Array.reduce((acc, tree) => acc * tree, 1, trees)

let solution: soltionTypes = (input, movements) => {
    movements
    ->Belt.Array.map(m =>
     getFootPrint({
        input: input,
        movement: m,
        row: 0,
        col: 0
     }, [])
      ->getTrees
      ->getNumberOfTrees)
    ->getMultiplyCountOfTrees
}

let movements: array<movement> = [
    {right: 1, down: 1},
    {right: 3, down: 1},
    {right: 5, down: 1},
    {right: 7, down: 1},
    {right: 1, down: 2}
]

parseInput()
 ->solution(movements)
 ->Js.log
