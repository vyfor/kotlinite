use lalrpop_util::lalrpop_mod;

lalrpop_mod!(pub grammar);

fn main() {
    let input = r#"
    fun test(arg: Boolean): Int { return 1; }

    const val a = 1;
    val b: Long? = 2;
    var c: Int = 3;

    if (true) {
      return 1;
    } else if (false) {
      return '2';
    } else {
      return "3";
    }
    "#;
    let parser = grammar::SourceParser::new();

    match parser.parse(input) {
        Ok(ast) => println!("AST: {:#?}", ast),
        Err(err) => eprintln!("Error: {:#?}", err),
    }
}
