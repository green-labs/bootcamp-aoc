// 그리드 생성
let createGrid = input =>
  input->Js.String2.split("\n")->Belt.Array.keep(line => line->Js.String.length > 0)

// down 값으로 Grid 의 row filter
let filterGridByDownValue = (grid, down) =>
  // grid->Belt.Array.keepWithIndex((_, index) => index !== 0 && mod(index, down) == 0)
  grid->Belt.Array.keepWithIndex((_, index) => mod(index, down) == 0)

// filtered 된 그리드와 right 값으로 해당 row 의 요소가 Tree 일 경우 1 씩 accumulate
let countEncounteredTrees = (gird, right) =>
  gird->Belt.Array.reduceWithIndex(0, (prev, curr, index) => {
    //let targetIndex = mod((index + 1) * right, curr->Js.String2.length)
    let targetIndex = mod(index * right, curr->Js.String2.length)
    let count = curr->Js.String2.charAt(targetIndex) === "#" ? 1 : 0
    prev + count
  })

type movement = {
  toRight: int,
  toDown: int,
}
let movement = (toRight, toDown) => {toRight: toRight, toDown: toDown}

let movements = [
  movement(1, 1),
  movement(3, 1),
  movement(5, 1),
  movement(7, 1),
  movement(1, 2),
]

let countEncounteredTreesFromMovement = (input, toDown, toRight) =>
  input
  ->createGrid
  ->filterGridByDownValue(toDown)
  ->countEncounteredTrees(toRight)

let multiplyNumberOfTreesEncountered = (input, movements) =>
  movements->Belt.Array.reduce(1, (prev, {toDown, toRight}) =>
    prev * countEncounteredTreesFromMovement(input, toDown, toRight)
  )

let input = Node.Fs.readFileAsUtf8Sync("input/Week1/Year2020Day3.sample.txt")

input->multiplyNumberOfTreesEncountered(movements)->Js.log
