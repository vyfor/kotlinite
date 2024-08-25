#[derive(Debug, PartialEq)]
pub enum Stmt {
    VarDecl {
        mutable: bool,
        name: String,
        ty: Option<Type>,
        init: Option<Box<Expr>>,
    },
    FunDecl {
        name: String,
        params: Vec<(String, Type)>,
        ty: Option<Type>,
        body: Box<Stmt>,
    },
    Block(Vec<Box<Stmt>>),
    ExprStmt(Box<Expr>),

    If {
        cond: Box<Expr>,
        then: Box<Stmt>,
        else_ifs: Vec<(Box<Expr>, Box<Stmt>)>,
        otherwise: Option<Box<Stmt>>,
    },
    While {
        cond: Box<Expr>,
        body: Box<Stmt>,
    },
    Return(Option<Box<Expr>>),

    Empty,
}

#[derive(Debug, PartialEq)]
pub enum Expr {
    Literal(Literal),
    Variable(String),
    BinaryOp(Box<Expr>, BinaryOperator, Box<Expr>),
    FunctionCall(String, Vec<Box<Expr>>),
}

#[derive(Debug, PartialEq)]
pub enum Literal {
    Integer,
    Float,
    String,
    Char,
    Boolean,
    Null,
    Custom(Box<Expr>),
}

#[derive(Debug, PartialEq, Clone)]
pub struct Type {
    pub kind: TypeKind,
    pub nullable: bool,
}

#[derive(Debug, PartialEq, Clone)]
pub enum TypeKind {
    // Boolean
    Boolean,

    // Numeric
    Byte,
    UByte,
    Short,
    UShort,
    Int,
    UInt,
    Long,
    ULong,
    Float,
    Double,

    // String
    Char,
    String,

    // Tuples
    Pair(Box<Type>, Box<Type>),
    Triple(Box<Type>, Box<Type>, Box<Type>),

    // Arrays
    Array(Box<Type>),
    ByteArray,
    UByteArray,
    ShortArray,
    UShortArray,
    IntArray,
    UIntArray,
    LongArray,
    ULongArray,
    FloatArray,
    DoubleArray,
    CharArray,
    BooleanArray,

    // Lists
    List(Box<Type>),
    MutableList(Box<Type>),

    // Sets
    Set(Box<Type>),
    MutableSet(Box<Type>),

    // Maps
    Map(Box<Type>, Box<Type>),
    MutableMap(Box<Type>, Box<Type>),

    // Ranges
    Range,

    // Other
    Any,
    Nothing,
    Unit,
    Enum(String, Box<Type>),
    Custom(String, Vec<Type>),
}

#[derive(Debug, PartialEq)]
pub enum BinaryOperator {
    Assign,
    AddAssign,
    SubtractAssign,
    MultiplyAssign,
    DivideAssign,
    ModuloAssign,
    Equal,
    NotEqual,
    ReferenceEqual,
    ReferenceNotEqual,
    LessThan,
    LessThanOrEqual,
    GreaterThan,
    GreaterThanOrEqual,
    And,
    Or,
    Xor,
    Shl,
    Shr,
    UShr,
    In,
    NotIn,
    Is,
    IsNot,
    RangeTo,
    RangeUntil,
    Add,
    Subtract,
    Multiply,
    Divide,
    Modulo,
}
