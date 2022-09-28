let input = Node.Fs.readFileAsUtf8Sync("input/Week1/Year2020Day3.txt")

input->Js.log

let x = ref(0)
let trees = ref(0)

input
->Js.String2.trim
->Js.String2.split("\n")
->Js.Array2.forEachi((row, index) => {
  if index > 0 {
    x := x.contents + 3
    if Js.String.charAt(mod(x.contents, Js.String.length(row)), row) === "#" {
      trees := trees.contents + 1
    }
  }
})

trees.contents->Js.log2("trees.")

input->Js.String2.trim->Js.String2.split("\n")->Js.Array2.reducei((trees2, row, index) => {
  if index > 0 {
    let col = index * 3
    if Js.String.charAt(mod(col, Js.String.length(row)), row) === "#" {
      trees2 + 1
    } else {
      trees2
    }
  } else {
    trees2
  }
}, 0)->Js.log2("trees.")
