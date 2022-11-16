type movement = {
    "right": int,
    "down": int
}

type rec getFootPrintTypes = (array<string>, movement, int, int) => array<string>

type soltionTypes = (array<string>, array<movement>) => int

let input = Node.Fs.readFileAsUtf8Sync("../../../../input/Week1/Year2020Day3.sample.txt") 
                -> Js.String.trim
                |> Js.String.split("\n")


// getFootPrint -> array ( 재귀 )
// getTrees -> array
// getNumberOfTrees -> int
// getSumOfTrees -> int
// solution -> int

let rec getFootPrint: getFootPrintTypes = (input, movement, row, col) => {
    
    switch (row >= Array.length(input)) {
    | true => []
    | false => {
        let footPrint = Js.String.get(input[row], mod(col, Js.String.length(input[row])))
        let nextRow = row + movement["down"];
        let nextCol = col + movement["right"];

        Js.Array.concat([footPrint], getFootPrint(input, movement, nextRow, nextCol))
      }
    }
}

let getTrees = (footPrints: array<string>) => {
    footPrints
    |> Js.Array.filter(footPrint => footPrint == "#")
} 

let getNumberOfTrees = (trees: array<string>) => Js.Array.length(trees)

let getSumOfTrees = (trees: array<int>) => Js.Array.reduce((acc, tree) => acc * tree, 1, trees)

let solution: soltionTypes = (input, movements) => {
    movements
    -> Belt.Array.map(m => getFootPrint(input, m, 0, 0)
    ->getTrees
    ->getNumberOfTrees)
    ->getSumOfTrees
}

let movements: array<movement> = [
    {"right": 1, "down": 1},
    {"right": 3, "down": 1},
    {"right": 5, "down": 1},
    {"right": 7, "down": 1},
    {"right": 1, "down": 2}
]

let result = solution(input, movements)

result->Js.log
