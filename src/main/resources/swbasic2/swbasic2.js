// @ts-check
/*
  Simple Web BASIC Interpreter 2 (SWBASIC2)
  Copyright (C) 2010 Yohanes Nugroho
  Copyright (C) 2019 Krisztian Konya

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Constants
 */
const KEYWORDS  = /(AND|CLEAR|CLS|DATA|DEF|DIM|ELSE|END|FOR|WIDTH|GOSUB|GOTO|IF|INPUT|LET|MOD|NEXT|NOT|ON|OR|PRINT|RANDOMIZE|SLEEP|READ|REM|RESTORE|RETURN|STEP|STOP|SYSTEM|THEN|TO|WHILE|WEND)/ig;
const FUNCTIONS = /^(ABS|ASC|ATN|CHR\$|SPACE\$|COS|EXP|INSTR|INT|LEFT\$|LEN|LOG|MID\$|POS|RIGHT\$|RND|SGN|SIN|SPC|SQR|STRING\$|STR\$|TAB|TAN|TIMER|VAL)$/i;

const TAB_CHARACTER      = " ";
const SPACE_CHARACTER    = " ";
const DEFAULT_TAB_SIZE   = 10;  // used for "," delimiters
const DEFAULT_ARRAY_SIZE = 10;
const MAX_GOSUB          = 255;
const TRUNCATE_DECIMALS  = true;
const MAX_DECIMALS       = 5;

// used in Random class
const RAND_A = 214013;
const RAND_C = 2531011;
const RAND_Z = 1 << 24;

/**
 * Utils class
 */
class Utils {
  static isNumber(m) {
    return typeof m === "number" || typeof m === "boolean";
  }

  static isSpace(c) {
    return c == " " || c == "\t" || c == "\v" || c == "\f";
  }

  static isDigit(c) {
    return c >= '0' && c <= '9';
  }

  static isDigitInBase(c, base) {
    if (base == 10) {
      return c >= '0' && c <= '9';
    }
    if (base == 8) {
      return c >= '0' && c <= '7';
    }
    if (base == 16) {
      let d = c;
      return (c >= '0' && c <= '9') || (d >= 'A' && d <= 'F');
    }
    return false;
  }

  static isAlpha(c) {
    return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
  }

  static isAlphanumeric(c) {
    return Utils.isAlpha(c) || Utils.isDigit(c);
  }

  static toFixed(num, fixed) {
    let re = new RegExp('^-?\\d+(?:\.\\d{0,' + (fixed || -1) + '})?');
    return num.toString().match(re)[0];
  }
}

/**
 * Token class
 */
class Token {
  constructor(text, type) {
    this.text = text;
    this.type = type;
  }

  toString() {
    return "[" + this.text + "," + this.type + "]";
  }

  getType() {
    return this.type;
  }

  getText() {
    return this.text;
  }
}

class Tokenizer {
  constructor(input) {
    this.input = input;
    this.error = "";
    this.tokens = [];
    this.theCurrentToken = 0;
    this.theLastToken = null; // last accepted token
    this.tokenize();
  }

  reset() {
    this.theCurrentToken = 0;
  }

  hasMoreTokens() {
    return (this.theCurrentToken < this.tokens.length);
  }

  nextToken() {
    return this.tokens[this.theCurrentToken++];
  }

  lastToken() {
    return this.theLastToken;
  }

  currentToken() {
    return this.tokens[this.theCurrentToken];
  }

  addToken(t) {
    this.tokens[this.tokens.length] = t;
  }

  isError() {
    return this.error;
  }

  tokenize() {
    let i = 0;
    let lineNum = ""; // used for error messages
    let isRemKeyword = false;
    let isDataKeyword = false;
    this.error = "";

    while (i < this.input.length) {
      let c = this.input[i];
      let next = (i + 1 < this.input.length) ? this.input[i + 1] : "";

      // handle REM
      if (isRemKeyword) {
        // jump to the end of the line
        while (i < this.input.length && c != "\r" && c != "\n") {
          i++;
          c = this.input[i];
        }
        isRemKeyword = false;
        continue;
      }

      // handle comma separated DATA values (strings without quote marks)
      if (isDataKeyword) {
        let buffer = "";
        let j = i;
        while (j < this.input.length && c != "\"" && c != "," && c != "\r" && c != "\n") {
          buffer += c;
          j++;
          c = this.input[j];
        }
        if (c != "\"") {  // NOTE: if c is a quotation mark, it will be handled as a quoted string later in the loop
          let n = Number(buffer);
          if (isNaN(n)) {
            buffer = buffer.replace(/^\s+/g, ''); // trim left only
            let t = new Token(buffer, "STRING");
            this.addToken(t);
            i = j;
            continue;
          }
        }
        c = this.input[i];  // restore c
      }

      if (c == ".") {
        let start = i;
        i++;
        c = this.input[i];
        while (i < this.input.length && Utils.isDigit(c)) {
          i++;
          c = this.input[i];
        }
        let number = this.input.substring(start, i);
        let t = new Token(number, "NUMBER");
        this.addToken(t);
        continue;
      }

      if (Utils.isDigit(c)) {
        let start = i;
        while (i < this.input.length && Utils.isDigit(c)) {
          i++;
          c = this.input[i];
        }
        if (i < this.input.length && c == ".") {
          i++;
          c = this.input[i];
          while (i < this.input.length && Utils.isDigit(c)) {
            i++;
            c = this.input[i];
          }
        }
        if (i < this.input.length && c == "E") {
          i++;
          c = this.input[i];
          if (i < this.input.length && (c == "+" || c == "-")) {
            i++;
            c = this.input[i];
            while (i < this.input.length && Utils.isDigit(c)) {
              i++;
              c = this.input[i];
            }
          }
        }
        let number = this.input.substring(start, i);
        if (c == "#" || c == "!") { // ignore 123#
          i++;
        }
        let t = new Token(number, "NUMBER");
        this.addToken(t);
        if (lineNum == "") {
          lineNum = number; // save as line number
        }
        continue;
      }

      if (c == "&") {
        let base = 10;
        let start = i + 1;
        if (next == "H") {
          base = 16;
          i += 2;
          start++;
        } else if (next == "O") {
          base = 8;
          i += 2;
          start++;
        } else {
          i++;
        }
        if (i < this.input.length) {
          c = this.input[i];
        } else {
          c = -1;
        }
        while (i < this.input.length && Utils.isDigitInBase(c, base)) {
          i++;
          c = this.input[i];
        }
        let number = this.input.substring(start, i);
        let n = parseInt(number, base);
        if (!isNaN(n)) {
          let t = new Token(n.toString(), "NUMBER");
          this.addToken(t);
          continue;
        }
      }

      if (c == "?") {
        i++;
        let t = new Token(c, "KEYWORD"); // alias for PRINT
        this.addToken(t);
        continue;
      }

      if (Utils.isAlpha(c)) {
        // get next alphanumeric string
        let buffer = "";

        while (i < this.input.length && Utils.isAlphanumeric(c)) {
          buffer += c;
          i++;
          c = this.input[i];
        }

        if (c == "$" || c == "!" || c == "#" || c == "%") {
          buffer += c;
          i++;
        }

        let regexp = new RegExp(KEYWORDS);
        let prevIndex = 0;
        let result;

        // iterate through matching keywords
        while ((result = regexp.exec(buffer)) !== null) {
          // start position of the current result
          let lastStartIndex = regexp.lastIndex - result[1].length;
          // any difference between start, and previous end? => there is something between them
          let diff = lastStartIndex - prevIndex;

          if (diff > 0) {
            // get this unknown string
            let other = buffer.substr(regexp.lastIndex - result[1].length - diff, diff);
            let r = other.match(FUNCTIONS);
            let t;
            // test if it is function, identifier or number
            if (r && r[0]) {
              t = new Token(other, "FUNCTION");
            } else if (isNaN(parseFloat(other))) {
              t = new Token(other, "IDENTIFIER");
            } else {
              t = new Token(other, "NUMBER");
            }
            this.addToken(t);
          }

          // get the matching keyword
          let keyword = result[0].toUpperCase();
          let t;
          if (keyword === "OR" || keyword === "AND" || keyword === "NOT") {
            t = new Token(keyword, "LOGICAL_OPERATOR");
          } else if (keyword === "MOD") {
            t = new Token(keyword, "MULT_OPERATOR");
          } else {
            t = new Token(keyword, "KEYWORD");
            if (keyword == "REM") {
              isRemKeyword = true;
            } else if (keyword == "DATA") {
              isDataKeyword = true;
            }
          }
          this.addToken(t);
          prevIndex = regexp.lastIndex;
          if (isRemKeyword || isDataKeyword) {
            break;
          }
        }

        // get the remaining string
        if (buffer.length > prevIndex && !isRemKeyword) {
          let other = buffer.substr(prevIndex, buffer.length);
          let r = other.match(FUNCTIONS);
          let t;
          // test if it is function, identifier or number
          if (isDataKeyword) {
            i -= buffer.length - prevIndex; // set character index back to the beginning of DATA
            continue;
          } else if (r && r[0]) {
            t = new Token(other, "FUNCTION");
          } else if (isNaN(parseFloat(other))) {
            t = new Token(other, "IDENTIFIER");
          } else {
            t = new Token(other, "NUMBER");
          }
          this.addToken(t);
        }
        continue;
      }

      if (c == "\"") {
        let start = i;
        i++;
        if (i >= this.input.length) {
          this.error = "Unterminated string";
          break;
        }
        c = this.input[i];
        while (i < this.input.length && c != "\"") {
          i++;
          c = this.input[i];
        }
        if (c != "\"") {
          this.error = "Unterminated string";
          break;
        }
        i++;
        let str = this.input.substring(start, i);
        let t = new Token(str.substring(1, str.length - 1), "STRING");
        this.addToken(t);
        continue;
      }

      if (Utils.isSpace(c)) {
        while (i < this.input.length && Utils.isSpace(c)) {
          i++;
          c = this.input[i];
        }
        continue;
      }

      if (c == "^") {
        i++;
        let t = new Token(c, "POW_OPERATOR");
        this.addToken(t);
        continue;
      }

      if (c == "*" || c == "/" || c == "^" || c == "\\") {
        i++;
        let t = new Token(c, "MULT_OPERATOR");
        this.addToken(t);
        continue;
      }

      if (c == "(") {
        i++;
        let t = new Token(c, "OPENPAREN");
        this.addToken(t);
        continue;
      }

      if (c == ")") {
        i++;
        let t = new Token(c, "CLOSEPAREN");
        this.addToken(t);
        continue;
      }

      if (c == "+" || c == "-") {
        i++;
        let t = new Token(c, "PLUSMINUS_OPERATOR");
        this.addToken(t);
        continue;
      }

      if (c == "'") {
        i++;
        let t = new Token(c, "COMMENT");
        this.addToken(t);
        isRemKeyword = true;
        continue;
      }

      if (c == ":") {
        i++;
        let t = new Token(c, "STATEMENT_DELIMITER");
        this.addToken(t);
        continue;
      }

      if (c == "," || c == ";") {
        i++;
        let t = new Token(c, "DELIMITER");
        this.addToken(t);
        continue;
      }

      if (c == "=") {
        // actually ambiguous, might be equality testing
        i++;
        let t = new Token(c, "ASSIGNMENT");
        this.addToken(t);
        continue;
      }

      if (c == ">" || c == "<") {
        i++;
        let t;
        if (c == "<" && next == ">") {
          i++;
          t = new Token("<>", "RELATIONAL");
        } else {
          if (next == '=') {
            i++;
            t = new Token(c + "=", "RELATIONAL");
          } else {
            t = new Token(c, "RELATIONAL");
          }
        }
        this.addToken(t);
        continue;
      }

      if (c == "\r" || c == "\n") {
        i++;
        if (c == "\r" && next == "\n") {
          i++;
        }
        let t = new Token("--", "ENDOFLINE");
        this.addToken(t);
        lineNum = "";
        isDataKeyword = false;
        continue;
      }
      let t = new Token(c, "CHARACTER");
      this.addToken(t);
      i++;
    }
    if (this.error) {
      throw "LINE: " + (lineNum == "" ? "??" : lineNum) + ", ERROR: " + this.error;
    }
  }

  toString() {
    let result = "current token: " + this.theCurrentToken + "\n";
    for (let i = 0; i < this.tokens.length; i++) {
      result += "<" + i + "> " + this.tokens[i].toString();
      result + "\n";
    }
    return result;
  }

  expect(type) {
    if (this.accept(type)) {
      return;
    }
    if (!this.hasMoreTokens()) {
      throw "Expected '" + type + "' but end of token found";
    }
    let t = this.tokens[this.theCurrentToken].getType();
    throw "Expected '" + type + "' but " + t + " found";
  }

  accept(type) {
    if (!this.hasMoreTokens()) {
      return false;
    }
    let t = this.tokens[this.theCurrentToken];
    if (t.getType() === type) {
      this.theLastToken = t;
      this.nextToken();
      return true;
    }
    return false;
  }

  acceptText(text) {
    if (!this.hasMoreTokens()) {
      return false;
    }
    let t = this.tokens[this.theCurrentToken];
    if (t.getText().toUpperCase() == text.toUpperCase()) {
      this.theLastToken = t;
      this.nextToken();
      return true;
    }
    return false;
  }

  willAccept(type) {
    // like accept(), but doesn't forward the token
    if (!this.hasMoreTokens()) {
      return false;
    }
    let t = this.tokens[this.theCurrentToken];
    if (t.getType() === type) {
      return true;
    }
    return false;
  }

  willAcceptText(text) {
    // like acceptText(), but doesn't forward the token
    if (!this.hasMoreTokens()) {
      return false;
    }
    let t = this.tokens[this.theCurrentToken];
    if (t.getText() == text) {
      return true;
    }
    return false;
  }

  unaccept() {
    if (this.theCurrentToken > 0)
      this.theCurrentToken--;
  }
}

/**
 * Line class
 */
class Line {
  constructor(number) {
    this.lineNumber = number;
    this.statements = [];
  }

  setStatements(statements) {
    this.statements = statements;
  }

  toString() {
    let result = "[ LINE# " + this.lineNumber + "] ";
    for (let i = 0; i < this.statements.length; i++) {
      let s = this.statements[i];
      result += "ST >> " + s.toString();
    }
    result += "[END OF LINE# " + this.lineNumber + "]";
    return result;
  }
}

/**
 * PNode class
 */
class PNode {
  constructor(type, text) {
    this.type = type;
    this.text = text;
    this.children = [];
  }

  getType() {
    return this.type;
  }

  getText() {
    return this.text;
  }

  addChild(c) {
    this.children[this.children.length] = c;
  }

  toString(level) {
    if (typeof level === "undefined") {
      level = 0;
    }
    let space = "";
    for (let i = 0; i < level; i++) {
      space += " ";
    }
    let result = space + "TYPE = <" + this.type + "> ";
    if (typeof this.text !== "undefined") {
      result += "TEXT = " + this.text;
    }
    result += "\n";
    for (let i = 0; i < this.children.length; i++) {
      result += this.children[i].toString(level + 1);
    }
    result += "\n";
    return result;
  }
}

/**
 * Parser class
 */
class Parser {
  constructor(text) {
    // prepare text: remove blank lines (incl. spaces, tabs), make uppercased
    this.text = text.replace(/^\s*[\r\n]/gm, "");//.toUpperCase(); SBLEND

    this.lines = []; // code that has line numbers goes here
    this.statements = []; // list of statements (flattened)
    this.labelIndex = [];
    this.defs = []
    this.functions = [];
    this.tokenizer = null;
    this.printFunction = null;

    this.functions["?"] = this.print_statement.bind(this);
    this.functions["CLEAR"] = this.clear_statement.bind(this);
    this.functions["CLS"] = this.cls_statement.bind(this);
    this.functions["DATA"] = this.data_statement.bind(this);
    this.functions["DEF"] = this.def_statement.bind(this);
    this.functions["DIM"] = this.dim_statement.bind(this);
    this.functions["END"] = this.end_statement.bind(this);
    this.functions["FOR"] = this.for_statement.bind(this);
    this.functions["WIDTH"] = this.width_statement.bind(this);
    this.functions["GOSUB"] = this.gosub_statement.bind(this);
    this.functions["GOTO"] = this.goto_statement.bind(this);
    this.functions["IF"] = this.if_statement.bind(this);
    this.functions["INPUT"] = this.input_statement.bind(this);
    this.functions["LET"] = this.let_statement.bind(this);
    this.functions["NEXT"] = this.next_statement.bind(this);
    this.functions["ON"] = this.on_statement.bind(this);
    this.functions["PRINT"] = this.print_statement.bind(this);
    this.functions["RANDOMIZE"] = this.randomize_statement.bind(this);
    this.functions["SLEEP"] = this.sleep_statement.bind(this);
    this.functions["READ"] = this.read_statement.bind(this);
    this.functions["REM"] = this.rem_statement.bind(this);
    this.functions["RESTORE"] = this.restore_statement.bind(this);
    this.functions["RETURN"] = this.return_statement.bind(this);
    this.functions["STOP"] = this.end_statement.bind(this); // this also ends program
    this.functions["SYSTEM"] = this.end_statement.bind(this); // this also ends program
    this.functions["WEND"] = this.wend_statement.bind(this);
    this.functions["WHILE"] = this.while_statement.bind(this);
  }

  // idx is for generating unique labels
  expandIf(dest, ifs, idx, line) {
    /*
      convert IF A THEN B ELSE C to:
      IF A THEN GOTO A_TRUE
      ELSE GOTO A_FALSE
      A_TRUE:
      THEN_STATEMENTS
      GOTO DONE
      A_FALSE:
      ELSE_STATEMENTS
      DONE:
    */

    if (ifs.children[1].getType() == "GOTO") {
      dest.push(ifs);
      return;
    }
    if (ifs.children[1].getType() == "GOSUB") {
      dest.push(ifs);
      return;
    }
    let uniqPos = line + "_" + idx;
    let labelTrue = uniqPos + "_TRUE";
    let labelFalse = uniqPos + "_FALSE";
    let labelDone = uniqPos + "_DONE";
    let newIf = new PNode("IF");
    newIf.addChild(ifs.children[0]);
    newIf.addChild(new PNode("GOTO", labelTrue));
    if (ifs.children.length == 3) {
      // only if there is ELSE part
      newIf.addChild(new PNode("GOTO", labelFalse));
    } else {
      newIf.addChild(new PNode("GOTO", labelDone));
    }
    dest.push(newIf);
    dest.push(new PNode("LINENUMBER", labelTrue));
    // add all children of THEN
    let thenPart = ifs.children[1];
    for (let i = 0; i < thenPart.children.length; i++) {
      // found another IF after THEN
      if (thenPart.children[i].getType() == "IF") {
        this.expandIf(dest, thenPart.children[i], idx + 1, line);
        dest.push(new PNode("LINENUMBER", labelDone));
        return;
      }
      dest.push(thenPart.children[i]);
    }
    // has ELSE part
    if (ifs.children.length == 3) {
      dest.push(new PNode("GOTO", labelDone));
      let elsePart = ifs.children[2];
      dest.push(new PNode("LINENUMBER", labelFalse));
      for (let i = 0; i < elsePart.children.length; i++) {
        if (elsePart.children[i].getType() == "IF") {
          this.expandIf(dest, elsePart.children[i], idx + 1, line);
          dest.push(new PNode("LINENUMBER", labelDone));
          return;
        }
        dest.push(elsePart.children[i]);
      }
    }
    dest.push(new PNode("LINENUMBER", labelDone));
  }

  flatten() {
    for (let i = 0; i < this.lines.length; i++) {
      let line = this.lines[i];
      this.statements.push(new PNode("LINENUMBER", line.lineNumber));
      for (let j = 0; j < line.statements.length; j++) {
        let statement = line.statements[j];
        if (statement.getType() == "IF") {
          this.expandIf(this.statements, statement, j, i);
        } else {
          this.statements.push(statement);
        }
      }
    }
    for (let i = 0; i < this.statements.length; i++) {
      let statement = this.statements[i];
      if (statement.getType() == "LINENUMBER") {
        if (typeof this.labelIndex[statement.getText()] === "undefined") {
          this.labelIndex[statement.getText()] = i;
        } else {
          throw "Duplicate line number " + statement.getText();
        }
      }
    }
  }

  accept(t) {
    return this.tokenizer.accept(t);
  }

  acceptText(t) {
    return this.tokenizer.acceptText(t);
  }

  willAccept(t) {
    return this.tokenizer.willAccept(t);
  }

  willAcceptText(t) {
    return this.tokenizer.willAcceptText(t);
  }

  lastText() {
    let t = this.tokenizer.lastToken();
    return t.getText();
  }

  hasMoreTokens() {
    return this.tokenizer.hasMoreTokens();
  }

  processLine(t) {
    let lineNum = parseInt(t.getText());
    let line = new Line(lineNum);
    line.setStatements(this.getStatementsTree());
    this.lines[this.lines.length] = line;
  }

  toString() {
    let result = "***\n";
    for (let i = 0; i < this.lines.length; i++) {
      let line = this.lines[i];
      result += line.toString();
      result += "---\n";
    }
    return result;
  }

  // calling order of expr methods means operator precedence

  atom() {
    if (this.accept("NUMBER")) {
      return new PNode("NUMBER", parseFloat(this.lastText()));
    } else if (this.willAccept("IDENTIFIER")) {
      return this.identifier();
    } else if (this.willAccept("FUNCTION")) {
      // function is just like identifier
      let identifier = this.identifier();
      identifier.type = "FUNCTION";
      return identifier;
    } else if (this.accept("STRING")) {
      return new PNode("STRING", this.lastText());
    } else if (this.accept("OPENPAREN")) {
      let n = this.expression();
      this.accept("CLOSEPAREN");
      return n;
    }
    let t = this.tokenizer.currentToken();
    if (typeof t === "undefined") {
      throw "Unexpected end of tokens";
    }
    throw "Unexpected token " + t.getText();
  }

  unaryExpr() {
    if (this.willAcceptText("NOT") && this.accept("LOGICAL_OPERATOR")) {
      let node = new PNode("NOT");
      node.addChild(this.atom());
      return node;
    }
    if (this.willAcceptText("+") && this.accept("PLUSMINUS_OPERATOR")) {
      let node = new PNode("UNARY_PLUS");
      node.addChild(this.atom());
      return node;
    }
    if (this.willAcceptText("-") && this.accept("PLUSMINUS_OPERATOR")) {
      let node = new PNode("UNARY_MINUS");
      node.addChild(this.atom());
      return node;
    }
    return this.atom();
  }

  powExpr() {
    let node = this.unaryExpr();
    while (this.accept("POW_OPERATOR")) {
      let opnode = new PNode(this.lastText());
      opnode.addChild(node);
      let rnode = this.unaryExpr();
      opnode.addChild(rnode);
      node = opnode;
    }
    return node;
  }

  multExpr() {
    let node = this.powExpr();
    // x -> (x)
    // 1*2/3  1-> (/ (* 1 2) 3)
    while (this.accept("MULT_OPERATOR")) {
      let opnode = new PNode(this.lastText());
      opnode.addChild(node);
      let rnode = this.powExpr();
      opnode.addChild(rnode);
      node = opnode;
    }
    return node;
  }

  plusExpr() {
    let node = this.multExpr();
    while (this.accept("PLUSMINUS_OPERATOR")) {
      let opnode = new PNode(this.lastText());
      opnode.addChild(node);
      let rnode = this.multExpr();
      opnode.addChild(rnode);
      node = opnode;
    }
    return node;
  }

  relational() {
    let node = this.plusExpr();
    while (this.accept("RELATIONAL") || this.acceptText("=")) {
      let opnode = new PNode(this.lastText());
      opnode.addChild(node);
      let rnode = this.plusExpr();
      opnode.addChild(rnode);
      node = opnode;
    }
    return node;
  }

  logical() {
    let node = this.relational();
    while (this.accept("LOGICAL_OPERATOR")) {
      let opnode = new PNode(this.lastText());
      opnode.addChild(node);
      let rnode = this.relational();
      opnode.addChild(rnode);
      node = opnode;
    }
    return node;
  }

  expression() {
    let node = new PNode("EXPRESSION");
    let child = this.logical();
    node.addChild(child);
    return node;
  }

  identifier() {
    let node = new PNode("VARIABLE");
    // builtin function
    if (!this.accept("IDENTIFIER") && !this.accept("FUNCTION")) {
      if (this.accept("KEYWORD") || this.accept("LOGICAL_OPERATOR")) {
        node.type = "KEYWORD";
        node.text = this.lastText().toUpperCase();
        return node;
      }
    }

    node.text = this.lastText().toUpperCase();
    if (this.accept("OPENPAREN")) {
      // defined function
      if (this.defs.includes(node.text)) {
        node.type = "DEF_FUNCTION";
        let expr = this.expression();
        node.addChild(expr);
        this.accept("CLOSEPAREN");
      // array
      } else {
        node.type = "ARRAY";
        while (this.hasMoreTokens()) {
          let expr = this.expression();
          node.addChild(expr);
          if (this.acceptText(",")) {
            continue;
          }
          if (this.accept("CLOSEPAREN")) {
            break;
          }
        }
      }
    }
    return node;
  }

  // For IF statement, if there is no else after the THEN, then
  // all statements before end of line is included in THEN part
  // if there is ELSE then the rest of the line goes to the ELSE part
  getRestOfLine(node) {
    // if not end of token
    while (this.hasMoreTokens() && !this.willAccept("ENDOFLINE") && !this.willAcceptText("ELSE")) {
      let statement = this.getStatement();
      node.addChild(statement);
      if (this.accept("STATEMENT_DELIMITER"))
        continue;
    }
  }

  getStatement() {
    if (this.accept("KEYWORD")) {
      let keyword = this.lastText().toUpperCase();
      if (this.functions[keyword]) {
        return this.functions[keyword](this);
      }
    }
    if (this.accept("COMMENT")) {
      return this.functions["REM"](this);
    }
    if (this.willAccept("IDENTIFIER")) {
      return this.let_statement(this);
    }
    throw "Statement error: " + this.tokenizer.lastToken();
  }

  getStatementsTree() {
    if (!this.hasMoreTokens()) {
      throw "Unexpected end of program";
    }
    let statements = [];
    while (this.hasMoreTokens() && !this.accept("ENDOFLINE")) {
      let statement = this.getStatement();
      if (typeof statement !== "undefined") {
        statements.push(statement);
      }
      this.accept("STATEMENT_DELIMITER");
      if (this.accept("COMMENT")) {
        while (this.hasMoreTokens() && !this.accept("ENDOFLINE")) {
          this.tokenizer.nextToken();
        }
        statements.push(new PNode("REM"));
        break;
      }
    }
    return statements;
  }

  parse() {
    this.tokenizer = new Tokenizer(this.text);
    let lineNum;
    try {
      while (this.hasMoreTokens()) {
        this.accept("ENDOFLINE");
        let t = this.tokenizer.nextToken();
        if (typeof t === "undefined") {
          throw "Token is undefined";
        }
        if (t.getType() === "NUMBER") {
          lineNum = t.getText();
          this.processLine(t);
        } else {
          throw "Statement must start with line number";
        }
        this.accept("ENDOFLINE");
      }
      this.flatten();
    } catch (e) {
      if (typeof lineNum === "undefined") {
        lineNum = this.lines.length > 0 ? "~" + this.lines[this.lines.length - 1].lineNumber : "??";
      }
      let errorMessage = "LINE " + lineNum + ", ERROR: ";
      errorMessage += e;
      console.log(errorMessage);
      // SBLEND
      if (this.printFunction) {
        this.printFunction(errorMessage, true);
      } else {
        throw errorMessage;
      }
    }
  }

  /* --- Statement methods ---*/

  clear_statement(self) {
    let node = new PNode("CLEAR");
    return node;
  }

  cls_statement(self) {
    let node = new PNode("CLS");
    return node;
  }

  data_statement(self) {
    let node = new PNode("DATA");
    if (this.willAccept("PLUSMINUS_OPERATOR") || this.willAccept("NUMBER") || this.willAccept("STRING")) {
      while (this.hasMoreTokens()) {
        if (this.accept("NUMBER")) {
          let text = this.lastText();
          if (!Utils.isNumber(text))
            text = parseFloat(text);
          node.addChild(new PNode("DATUM", text));
        } else if (this.accept("PLUSMINUS_OPERATOR")) {
          let operator = this.lastText();
          if (this.accept("NUMBER")) {
            let text = operator + this.lastText();
            if (!Utils.isNumber(text))
              text = parseFloat(text);
            node.addChild(new PNode("DATUM", text));
          }
        } else if (this.accept("STRING")) {
          node.addChild(new PNode("DATUM", this.lastText()));
        } else {
          throw "Unexpected token " + this.lastText();
        }
        if (!this.acceptText(","))
          break;
      }
    }
    return node;
  }

  def_statement(self) {
    let node = new PNode("DEF");
    if (!this.accept("IDENTIFIER")) {
      throw "Expected function identifier after DEF";
    }
    let fname = this.lastText().toUpperCase();
    if (fname.length < 3 || !fname.startsWith("FN")) {
      throw "DEF function name must start with FN";
    }
    let identifier = new PNode("DEF_FN_IDENTIFIER");
    identifier.text = fname;
    let param;
    let func;
    node.addChild(identifier);
    if (this.accept("OPENPAREN") && this.accept("IDENTIFIER") && this.willAccept("CLOSEPAREN")) {
      param = new PNode("DEF_FN_PARAM");
      param.text = this.lastText().toUpperCase();
      node.addChild(param);
      this.accept("CLOSEPAREN");
    } else {
      throw "Expected function parameter in DEF statement";
    }
    if (this.acceptText("=")) {
      func = this.expression();
      func.type = "DEF_FN_BODY";
      node.addChild(func);
    } else {
      throw "Expected function body in DEF statement"
    }
    this.defs.push(fname);
    return node;
  }

  dim_statement(self) {
    let node = new PNode("DIM");
    if (this.willAccept("IDENTIFIER")) {
      while (this.hasMoreTokens()) {
        let identifier = this.identifier();
        if (identifier.getType() === "KEYWORD") {
          throw "Keyword is not allowed to use in identifier: " + identifier.getText();
        }
        identifier.type = "ARRAY";
        node.addChild(identifier);
        if (this.acceptText(",")) {
          continue;
        }
        if (this.end_of_line_statement()) {
          break;
        }
      }
      return node;
    }
    throw "Expected identifer after DIM";
  }

  end_statement(self) {
    let node = new PNode("END");
    return node;
  }

  end_of_line_statement(self) {
    // no keyword for this statement
    if (this.willAccept("ENDOFLINE") ||
      this.willAccept("STATEMENT_DELIMITER") ||
      this.willAcceptText("ELSE") ||
      this.willAccept("COMMENT")) {
      return true;
    }
    return false;
  }

  for_statement(self) {
    let node = new PNode("FOR");
    if (!this.accept("IDENTIFIER")) {
      throw "Expected identifier in FOR statement";
    }
    let variable = new PNode("VARIABLE");
    variable.text = this.lastText().toUpperCase();
    if (!this.acceptText("=")) {
      throw "Expected '=' ";
    }
    node.addChild(variable);
    let expr = this.expression();
    if (!this.acceptText("TO")) {
      throw "Expected TO in FOR statement ";
    }
    node.addChild(expr);
    let limitExpr = this.expression();
    node.addChild(limitExpr);
    if (this.acceptText("STEP")) {
      let stepExpr = this.expression();
      node.addChild(stepExpr);
    }
    return node;
  }

  width_statement(self) {
    let node = new PNode("WIDTH");
    if (this.accept("NUMBER")) {
      node.text = this.lastText().toUpperCase();
      return node;
    }
    throw "WIDTH should be followed by number";
  }

  goto_statement(self) {
    let node = new PNode("GOTO");
    if (this.accept("NUMBER")) {
      node.text = this.lastText().toUpperCase();
      return node;
    }
    throw "GOTO should be followed by number";
  }

  gosub_statement(self) {
    let node = new PNode("GOSUB");
    if (this.accept("NUMBER")) {
      node.text = this.lastText().toUpperCase();
      return node;
    }
    throw "GOSUB should be followed by number";
  }

  if_statement(self) {
    let node = new PNode("IF");
    let expr = this.expression();
    node.addChild(expr);
    if (this.acceptText("THEN")) {

      let thenPart = new PNode("THEN");
      if (this.willAccept("NUMBER")) {
        this.accept("NUMBER");
        let statement = new PNode("GOTO");
        statement.text = this.lastText().toUpperCase();
        thenPart.addChild(statement);
      } else if (!this.willAcceptText("ELSE")) {
        let thenStatement = this.getStatement();
        this.accept("STATEMENT_DELIMITER");
        thenPart.addChild(thenStatement);
        this.getRestOfLine(thenPart);
      }

      node.addChild(thenPart);

      if (this.acceptText("ELSE")) {
        let elsePart = new PNode("ELSE");
        if (this.willAccept("NUMBER")) {
          this.accept("NUMBER");
          let gotoStatement = new PNode("GOTO");
          gotoStatement.text = this.lastText().toUpperCase();
          elsePart.addChild(gotoStatement);
        } else {
          let elseStatement = this.getStatement();
          this.accept("STATEMENT_DELIMITER");
          elsePart.addChild(elseStatement);
          this.getRestOfLine(elsePart);
        }
        node.addChild(elsePart);
        return node;
      }
      return node;
    } else if (this.accept("GOTO")) {
      let gotoPart = new PNode("GOTO");
      if (this.accept("NUMBER")) {
        gotoPart.text = this.lastText().toUpperCase();
        node.addChild(gotoPart);
        // ignore rest of line
        let ignore = new PNode("_");
        this.accept("STATEMENT_DELIMITER");
        this.getRestOfLine(ignore);
        return node;
      } else {
        throw "Expected number after GOTO";
      }
    }
    throw "Expected THEN or GOTO in IF statement";
  }

  input_statement(self) {
    let node = new PNode("INPUT");
    let questionMark = "? ";
    if (this.accept("STRING")) {
      questionMark = this.lastText() + questionMark;
    }
    let query = new PNode("STRING", questionMark);
    node.addChild(query);
    while (this.hasMoreTokens()) {
      if (this.end_of_line_statement())
        break;
      if (this.tokenizer.accept("KEYWORD")) {
        throw "Unexpected keyword " + this.lastText();
      }
      if (this.tokenizer.accept("DELIMITER")) {
        continue;
      }
      let identifier = this.identifier();
      node.addChild(identifier);
    }
    return node;
  }

  let_statement(self) {
    let identifier = this.identifier();
    if (identifier.getType() === "KEYWORD") {
      throw "Keyword is not allowed to use in identifier: " + identifier.getText();
    }
    if (this.acceptText("=")) {
      let node = this.expression();
      let anode = new PNode("ASSIGNMENT");
      anode.addChild(identifier);
      anode.addChild(node);
      return anode;
    }
    throw "Expected '=' after " + identifier.getText();
  }

  next_statement(self) {
    let node = new PNode("NEXT");
    if (this.accept("IDENTIFIER")) {
      while (this.hasMoreTokens()) {
        node.addChild(new PNode("IDENTIFIER", this.lastText().toUpperCase()));
        if (!this.acceptText(","))
          break;
        if (!this.accept("IDENTIFIER"))
          break;
      }
    }
    return node;
  }

  on_statement(self) {
    let node = new PNode("ON");
    let expr = this.expression();
    node.addChild(expr);
    // with GOTO
    if (this.acceptText("GOTO")) {
      let gotoPart = new PNode("GOTO");
      if (this.willAccept("NUMBER")) {
        while (this.hasMoreTokens()) {
          if (this.accept("NUMBER")) {
            let text = this.lastText();
            if (!Utils.isNumber(text)) {
              text = parseFloat(text);
            }
            gotoPart.addChild(new PNode("LABEL", text));
          } else {
            throw "Unexpected token " + this.lastText();
          }
          if (!this.acceptText(","))
            break;
        }
        node.addChild(gotoPart);
        return node;
      } else {
        throw "Expected number after GOTO";
      }
    // with GOSUB
    } else if (this.acceptText("GOSUB")) {
      let gosubPart = new PNode("GOSUB");
      if (this.willAccept("NUMBER")) {
        while (this.hasMoreTokens()) {
          if (this.accept("NUMBER")) {
            let text = this.lastText();
            if (!Utils.isNumber(text)) {
              text = parseFloat(text);
            }
            gosubPart.addChild(new PNode("LABEL", text));
          } else {
            throw "Unexpected token " + this.lastText();
          }
          if (!this.acceptText(","))
            break;
        }
        node.addChild(gosubPart);
        return node;
      } else {
        throw "Expected number after GOSUB";
      }
    }
  }

  print_statement(self) {
    let node = new PNode("PRINT");
    while (this.hasMoreTokens()) {
      if (this.end_of_line_statement()) {
        break;
      }
      if (this.accept("KEYWORD")) {
        throw "Unexpected keyword " + this.lastText();
      }
      if (this.accept("DELIMITER")) {
        node.addChild(new PNode("DELIMITER", this.lastText()));
        continue;
      }
      if (this.end_of_line_statement()) {
        break;
      }
      let expr = this.expression();
      node.addChild(expr);
    }
    return node;
  }

  randomize_statement(self) {
    let node = new PNode("RANDOMIZE");
    node.addChild(this.expression());
    return node;
  }

  sleep_statement(self) {
    let node = new PNode("SLEEP");
    node.addChild(this.expression());
    return node;
  }

  read_statement(self) {
    let node = new PNode("READ");
    while (this.hasMoreTokens()) {
      if (this.end_of_line_statement())
        break;
      if (this.tokenizer.accept("KEYWORD")) {
        throw "Unexpected keyword " + this.lastText();
      }
      if (this.tokenizer.accept("DELIMITER")) {
        continue;
      }
      let identifier = this.identifier();
      node.addChild(identifier);
    }
    return node;
  }

  rem_statement(self) {
    let node = new PNode("REM");
    while (this.hasMoreTokens() && !this.willAccept("ENDOFLINE")) {
      this.tokenizer.nextToken();
    }
    return node;
  }

  restore_statement(self) {
    let node = new PNode("RESTORE");
    return node;
  }

  return_statement(self) {
    let node = new PNode("RETURN");
    return node;
  }

  wend_statement(self) {
    let node = new PNode("WEND");
    return node;
  }

  while_statement(self) {
    let node = new PNode("WHILE");
    let expr = this.expression();
    node.addChild(expr);
    return node;
  }
}


/**
 * Variable class
 */
class Variable {
  constructor(name) {
    // variable name with [] means its an array
    this.name = (name == null ? name : name.toUpperCase()); // SBLEND CHECK 1++2
    this.value = null;
    this.bounds = null;
    this.mult = null;
  }

  setBounds(dbounds) {
    this.value = [];
    this.bounds = []; // a(4,3), bounds = 4,3
    this.mult = []; // a(4,3), mult 1,4
    let m = 1;
    for (let i = 0; i < dbounds.length; i++) {
      this.bounds[i] = dbounds[i] + 1;
      this.mult[i] = m;
      m *= this.bounds[i];
    }
    let val;
    if (this.name[this.name.length - 3] == "$") {
      val = "";
    } else {
      val = 0;
    }
    for (let i = 0; i < m; i++) {
      this.value[i] = val;
    }
  }

  getDimension() {
    return this.bounds == null ? 0 : this.bounds.length;
  }

  computePos(indices) {
    let pos = 0;
    for (let i = 0; i < indices.length; i++) {
      pos += indices[i] * this.mult[i];
    }
    return pos;
  }

  inBounds(indices) {
    if (indices.length != this.bounds.length)
      return false;
    for (let i = 0; i < indices.length; i++) {
      if (indices[i] < 0 || indices[i] >= this.bounds[i]) {
        return false;
      }
    }
    return true;
  }

  setValue(value, indices) {
    if (typeof indices === "undefined") {
      this.value = value;
      return;
    }
    this.value[this.computePos(indices)] = value;
  }

  getValue(indices) {
    if (typeof indices === "undefined") {
      return this.value;
    }
    return this.value[this.computePos(indices)];
  }
}

/**
 * Random class
 * http://support.microsoft.com/kb/28150
 */
class Random {
  constructor(seed) {
    this.seed = seed; // keep the initial seed
    this.x0 = seed;

    if (typeof seed === "undefined") {
      this.seed = new Date().getTime();
      this.x0 = this.seed;
    }
  }

  setSeed(s) {
    this.seed = s;
    this.x0 = s;
  }

  random() {
    this.x0 = (this.x0 * RAND_A + RAND_C) % (RAND_Z);
    return Math.abs(this.x0) / RAND_Z;
  }
}

/**
 * Interpreter class
 */
class Interpreter {
  constructor(parser) {
    this.parser = parser;
    this.ifunctions = [];
    this.variables = [];
    this.gosubStack = [];
    this.forStack = [];
    this.whileStack = [];
    this.forInfo = [];
    this.defInfo = [];
    this.printFunction = null;
    this.sleepFunction = null;
    this.inkeyFunction = null;
    this.numberInputFunction = null;
    this.stringInputFunction = null;
    this.clsFunction = null;
    this.endFunction = null;
    this.breakFunction = null;
    this.lastRandom = 0;
    this.inputStack = [];
    this.lastPoint = 0;
    this.data = [];
    this.dataPointer = 0;
    this.lastInputVar = 0;
    this.ended = false;
    this.printPos = 0;
    this.random = new Random();
    this.lastRandom = this.random.random();

    this.ifunctions["?"] = this.print_statement.bind(this);
    this.ifunctions["ASSIGNMENT"] = this.assignment_statement.bind(this);
    this.ifunctions["CLEAR"] = this.clear_statement.bind(this);
    this.ifunctions["CLS"] = this.cls_statement.bind(this);
    this.ifunctions["DATA"] = this.data_statement.bind(this);
    this.ifunctions["DEF"] = this.def_statement.bind(this);
    this.ifunctions["DIM"] = this.dim_statement.bind(this);
    this.ifunctions["END"] = this.end_statement.bind(this);
    this.ifunctions["FOR"] = this.for_statement.bind(this);
    this.ifunctions["WIDTH"] = this.width_statement.bind(this);
    this.ifunctions["GOSUB"] = this.gosub_statement.bind(this);
    this.ifunctions["GOTO"] = this.goto_statement.bind(this);
    this.ifunctions["IF"] = this.if_statement.bind(this);
    this.ifunctions["INPUT"] = this.input_statement.bind(this);
    this.ifunctions["NEXT"] = this.next_statement.bind(this);
    this.ifunctions["ON"] = this.on_statement.bind(this);
    this.ifunctions["PRINT"] = this.print_statement.bind(this);
    this.ifunctions["RANDOMIZE"] = this.randomize_statement.bind(this);
    this.ifunctions["SLEEP"] = this.sleep_statement.bind(this);
    this.ifunctions["READ"] = this.read_statement.bind(this);
    this.ifunctions["RESTORE"] = this.restore_statement.bind(this);
    this.ifunctions["RETURN"] = this.return_statement.bind(this);
    this.ifunctions["WEND"] = this.wend_statement.bind(this);
    this.ifunctions["WHILE"] = this.while_statement.bind(this);
  }

  pushInput(v) {
    if (Array.isArray(v)) {
      this.inputStack.push(...v);
    } else {
      this.inputStack.push(v);
    }
  }

  resumeInput() {
    this.printPos = 0;
    this.run(this.lastPoint);
  }

  getArrayIndices(identifier) {
    let indices = [];
    for (let i = 0; i < identifier.children.length; i++) {
      indices[i] = Math.floor(this.evalExpr(identifier.children[i]));
    }
    return indices;
  }

  getNextLine(i) {
    return i + 1;
  }

  ensureExist(identifier) {
    let name = identifier.getText();
    if (identifier.getType() == "ARRAY") {
      name += "[]";
      if (typeof this.variables[name] === "undefined") {
        let v = new Variable(name);
        let bounds = [];
        bounds[0] = DEFAULT_ARRAY_SIZE;
        v.setBounds(bounds);
        this.variables[name] = v;
      }
    } else {
      if (typeof this.variables[name] === "undefined") {
        let v = new Variable(name);
        this.variables[name] = v;
        if (name[name.length - 1] == "$") {
          v.setValue("");
        } else {
          v.setValue(0);
        }
      }
    }
  }

  getValue(identifier) {
    let name = identifier.getText();
    this.ensureExist(identifier);
    if (identifier.getType() == "ARRAY") {
      name += "[]";
      let variable = this.variables[name];
      let indices = this.getArrayIndices(identifier);
      if (!variable.inBounds(indices)) {
        throw "Subscript out of range";
      }
      return variable.getValue(indices);
    } else {
      let variable = this.variables[name];
      return variable.getValue();
    }
  }

  setNumericValue(name, value) {
    if (typeof this.variables[name] === "undefined") {
      let v = new Variable(name);
      this.variables[name] = v;
      v.setValue(value);
    }
    let v = this.variables[name];
    v.setValue(value);
  }

  getNumericValue(name, value) {
    if (typeof this.variables[name] === "undefined") {
      let v = new Variable(name);
      this.variables[name] = v;
      v.setValue(0);
      return 0;
    }
    let v = this.variables[name];
    return v.getValue();
  }

  setValue(identifier, value) {
    let name = identifier.getText();
    this.ensureExist(identifier);
    if (identifier.getType() == "ARRAY") {
      name += "[]";
      let variable = this.variables[name];
      let indices = this.getArrayIndices(identifier);
      if (!variable.inBounds(indices)) {
        throw "Subscript out of range";
      }
      return variable.setValue(value, indices);
    } else {
      let variable = this.variables[name];
      variable.setValue(value);
    }
  }

  expectParam(f, n, m) {
    if (typeof m === "undefined") {
      if (f.children.length != n) {
        throw "Function '" + f.text + "' expects " + n + " parameter(s), but got " + f.children.length;
      }
    }
    if (f.children.length < n || f.children.length > m) {
      throw "Function '" + f.text + "' expects " + n + " to " + m + "parameters, but got " + f.children.length;
    }
  }

  evalFunction(f) {
    let funcName = f.getText();
    let paramCount = f.children.length;
    switch (funcName) {
      case "ABS": {
        this.expectParam(f, 1);
        let val = this.evalExpr(f.children[0]);
        if (Utils.isNumber(val)) {
          return Math.abs(val);
        }
        throw "Type mismatch for function ABS";
      }
      case "ASC": {
        this.expectParam(f, 1);
        let val = this.evalExpr(f.children[0]);
        if (typeof val === "string") {
          return val.charCodeAt(0);
        }
        throw "Type mismatch for function ASC";
      }
      case "ATN": {
        this.expectParam(f, 1);
        let val = this.evalExpr(f.children[0]);
        if (Utils.isNumber(val)) {
          return Math.atan(val);
        }
        throw "Type mismatch for function ATN";
      }
      case "CHR$": {
        this.expectParam(f, 1);
        let val = this.evalExpr(f.children[0]);
        if (Utils.isNumber(val)) {
          return String.fromCharCode(val);
        }
        throw "Type mismatch for function CHR$";
      }
      case "SPACE$": {
        this.expectParam(f, 1);
        let val = this.evalExpr(f.children[0]);
        if (Utils.isNumber(val)) {
          return SPACE_CHARACTER.repeat(val);
        }
        throw "Type mismatch for function SPACE$";
      }
      case "COS": {
        this.expectParam(f, 1);
        let val = this.evalExpr(f.children[0]);
        if (Utils.isNumber(val)) {
          return Math.cos(val);
        }
        throw "Type mismatch for function COS";
      }
      case "EXP": {
        this.expectParam(f, 1);
        let val = this.evalExpr(f.children[0]);
        if (Utils.isNumber(val)) {
          return Math.exp(val);
        }
        throw "Type mismatch for function EXP";
      }
      case "INSTR": {
        this.expectParam(f, 2, 3);
        let val = this.evalExpr(f.children[0]);
        if (typeof val === "string") {
          let substr = this.evalExpr(f.children[1]);
          if (typeof substr == "string") {
            return val.indexOf(substr) + 1;
          }
        } else if (Utils.isNumber(val)) {
          let str = this.evalExpr(f.children[1]);
          if (typeof str == "string") {
            let substr = this.evalExpr(f.children[2]);
            if (typeof substr == "string") {
              return str.indexOf(substr) + 1;
            }
          }
        }
        throw "Type mismatch for function INSTR";
      }
      case "INT": {
        this.expectParam(f, 1);
        let val = this.evalExpr(f.children[0]);
        if (Utils.isNumber(val)) {
          return Math.floor(val);
        }
        throw "Type mismatch for function INT";
      }
      case "LEFT$": {
        this.expectParam(f, 2);
        let val = this.evalExpr(f.children[0]);
        if (typeof val === "string") {
          let n = this.evalExpr(f.children[1]);
          if (Utils.isNumber(n)) {
            return val.substring(0, n);
          }
        }
        throw "Type mismatch for function LEFT$";
      }
      case "LEN": {
        this.expectParam(f, 1);
        let val = this.evalExpr(f.children[0]);
        if (typeof val === "string") {
          return val.length;
        }
        throw "Type mismatch for function LEN";
      }
      case "LOG": {
        this.expectParam(f, 1);
        let val = this.evalExpr(f.children[0]);
        if (Utils.isNumber(val)) {
          return Math.log(val);
        }
        throw "Type mismatch for function LOG";
      }
      case "MID$": {
        this.expectParam(f, 2, 3);
        let val = this.evalExpr(f.children[0]);
        if (typeof val === "string") {
          let n = this.evalExpr(f.children[1]);
          if (Utils.isNumber(n)) {
            if (paramCount == 2) {
              return val.substring(n - 1);
            }
            let m = this.evalExpr(f.children[2]);
            if (Utils.isNumber(m)) {
              return val.substr(n - 1, m);
            }
          }
        }
        throw "Type mismatch for function MID$";
      }
      case "POS": {
        this.expectParam(f, 1);  // dummy parameter, mandatory
        let val = this.evalExpr(f.children[0]);
        if (Utils.isNumber(val)) {
          return this.printPos;
        }
        throw "Type mismatch for function POS";
      }
      case "RIGHT$": {
        this.expectParam(f, 2);
        let val = this.evalExpr(f.children[0]);
        if (typeof val === "string") {
          let n = this.evalExpr(f.children[1]);
          if (Utils.isNumber(n)) {
            return val.substring(val.length - n);
          }
        }
        throw "Type mismatch for function RIGHT$";
      }
      case "RND": {
        this.expectParam(f, 0, 1);
        if (paramCount == 0) {
          this.lastRandom = this.random.random();
          return this.lastRandom;
        } else {
          let n = this.evalExpr(f.children[0]);
          if (Utils.isNumber(n)) {
            if (n < 0) {
              this.random.setSeed(n);
            }
            if (n != 0) {
              this.lastRandom = this.random.random();
            }
            return this.lastRandom;
          }
        }
        throw "Type mismatch for function RND";
      }
      case "SGN": {
        this.expectParam(f, 1);
        let val = this.evalExpr(f.children[0]);
        if (Utils.isNumber(val)) {
          if (val > 0) {
            return 1;
          } else if (val < 0) {
            return -1;
          } else {
            return 0;
          }
        }
        throw "Type mismatch for function SGN";
      }
      case "SIN": {
        this.expectParam(f, 1);
        let val = this.evalExpr(f.children[0]);
        if (typeof val !== "string") {
          return Math.sin(val);
        }
        throw "Type mismatch for function SIN";
      }
      case "SPC": {
        this.expectParam(f, 1);
        let val = this.evalExpr(f.children[0]);
        if (Utils.isNumber(val)) {
          return SPACE_CHARACTER.repeat(val);
        }
        throw "Type mismatch for function SPC";
      }
      case "SQR": {
        this.expectParam(f, 1);
        let val = this.evalExpr(f.children[0]);
        if (Utils.isNumber(val)) {
          return Math.sqrt(val);
        }
        throw "Type mismatch for function SQR";
      }
      case "STR$": {
        this.expectParam(f, 1);
        let val = this.evalExpr(f.children[0]);
        if (Utils.isNumber(val)) {
          return val < 0 ? val.toString() : " " + val.toString();
        }
        throw "Type mismatch for function STR$";
      }
      case "STRING$": {
        this.expectParam(f, 2);
        let count = this.evalExpr(f.children[0]);
        if (Utils.isNumber(count)) {
          let val = this.evalExpr(f.children[1]);
          let c = "";
          if (Utils.isNumber(val)) {
            c = String.fromCharCode(val);
          } else {
            if (val.length < 1)
              throw "Illegal function call for STRING$";
            c = val[0];
          }
          let result = "";
          for (let i = 0; i < count; i++) {
            result += c;
          }
          return result;
        }
        throw "Type mismatch for function STRING$";
      }
      case "TAB": {
        this.expectParam(f, 1);
        let val = this.evalExpr(f.children[0]);
        if (Utils.isNumber(val)) {
          return val; // returns the parameter value itself, used in print statement
        }
        throw "Type mismatch for function TAB";
      }
      case "TAN": {
        this.expectParam(f, 1);
        let val = this.evalExpr(f.children[0]);
        if (Utils.isNumber(val)) {
          return Math.tan(val);
        }
        throw "Type mismatch for function TAN";
      }
      case "TIMER": {
        this.expectParam(f, 0);
        return Math.floor(new Date().getTime() / 1000);
      }
      case "VAL": {
        this.expectParam(f, 1);
        let val = this.evalExpr(f.children[0]);
        if (typeof val === "string") {
          var valFuncResult = parseFloat(val);
          return isNaN(valFuncResult) ? 0 : valFuncResult;
        }
        throw "Type mismatch for function VAL";
      }
    }
  }

  evalDefFunction(f) {
    let funcName = f.getText();
    if (typeof this.defInfo[funcName] !== "undefined") {
      let info = this.defInfo[funcName];
      let paramName = info.param.getText();
      let paramValue = this.evalExpr(f.children[0]);
      if (typeof paramValue !== "number") {
        throw "Type mismatch for defined function (numeric only)";
      }
      let func = info.func;
      // backup original value of variable
      let origValue = this.getNumericValue(paramName);
      // set param value and evaluate the function
      this.setNumericValue(paramName, paramValue);
      let val = this.evalExpr(func.children[0]);
      // restore original
      this.setNumericValue(paramName, origValue);
      return val;
    } else {
      throw "Function not found " + funcName;
    }
  }

  evalExpr(expr) {
    let type = expr.getType();

    if (type == "EXPRESSION") {
      expr = expr.children[0];
      type = expr.getType();
      return this.evalExpr(expr);
    }

    let text = expr.getText();

    if (type == "STRING") {
      return text;
    }

    if (type == "NUMBER") {
      if (Utils.isNumber(text)) {
        return text;
      }
      if (text.indexOf(".") >= 0) {
        return parseFloat(text);
      } else {
        return parseInt(text);
      }
    }

    let left = expr.children[0];
    let right = expr.children[1];
    let leftVal;
    if (typeof left !== "undefined") {
      leftVal = this.evalExpr(left);
    }
    let rightVal;
    if (typeof right !== "undefined") {
      rightVal = this.evalExpr(right);
    }

    if (typeof leftVal == "string") leftVal = leftVal.toUpperCase(); // SBLEND
    if (typeof rightVal == "string") rightVal = rightVal.toUpperCase(); // SBLEND

    if (text === "=") {
      return leftVal == rightVal;
    }

    switch (type.toUpperCase()) {
      case "*":
        return leftVal * rightVal;
      case "^":
        return Math.pow(leftVal, rightVal);
      case "/":
        let divisionResult = leftVal / rightVal;
        if (!isFinite(divisionResult)) {
          throw "Division by zero";
        }
        return divisionResult;
      case "\\":
        let intDivisionResult = leftVal / rightVal;
        if (!isFinite(intDivisionResult)) {
          throw "Division by zero";
        }
        return Math.floor(intDivisionResult);
      case "+":
        return leftVal + rightVal;
      case "-":
        return leftVal - rightVal;
      case "=":
        return this.boolToNumeric(leftVal == rightVal);
      case ">":
        return this.boolToNumeric(leftVal > rightVal);
      case "<":
        return this.boolToNumeric(leftVal < rightVal);
      case "<=":
        return this.boolToNumeric(leftVal <= rightVal);
      case ">=":
        return this.boolToNumeric(leftVal >= rightVal);
      case "<>":
        return this.boolToNumeric(leftVal != rightVal);
      case "AND":
        return leftVal & rightVal;
      case "OR":
        return leftVal | rightVal;
      case "MOD":
        return leftVal % rightVal;
      case "NOT":
        return ~leftVal;
      case "UNARY_MINUS":
        return -leftVal;
      case "UNARY_PLUS":
        return leftVal;
      case "FUNCTION":
        return this.evalFunction(expr);
      case "DEF_FUNCTION":
        return this.evalDefFunction(expr);
      default:
        return this.getValue(expr);
    }
  }

  boolToNumeric(b) {
    return b ? -1 : 0;
  }

  findLabel(label) {
    let labelIndex = this.parser.labelIndex;
    if (typeof labelIndex[label] === "undefined") {
      throw "GOTO destination " + label + " not found";
    }
    return labelIndex[label];
  }

  findNext(idx, varname) {
    let length = this.parser.statements.length;
    for (let i = idx + 1; i < length; i++) {
      let statement = this.parser.statements[i];
      if (statement.getType() == "NEXT") {
        // next without variable match with anything
        if (statement.children.length == 0) {
          return i;
        }
        let nextLength = statement.children.length;
        for (let j = 0; j < nextLength; j++) {
          if (statement.children[j].text.toUpperCase() === varname.toUpperCase()) {
            return i;
          }
        }
      }
    }
    throw "FOR without NEXT";
  }

  findWend(idx, varname) {
    let length = this.parser.statements.length;
    for (let i = idx + 1; i < length; i++) {
      let statement = this.parser.statements[i];
      if (statement.getType() == "WEND") {
        return i;
      }
    }
    throw "WHILE without WEND";
  }

  run(idx) {
    let length = this.parser.statements.length;
    try {
      while (idx < length) {
        let newidx = -1;
        try {
          let statement = this.parser.statements[idx];
          let type = statement.getType();
          if (type === "LINENUMBER") {
            newidx = idx + 1;
          } else if (typeof this.ifunctions[type] !== "undefined") {
            newidx = this.ifunctions[type](this, idx);
          } else {
            newidx = idx + 1;
          }
        } catch (e) {
          let errorMessage = "LINE " + this.findLineNumber(idx) + ", ERROR: ";
          errorMessage += e;
          console.log(errorMessage);
          // SBLEND
          if (this.printFunction) {
            this.printFunction(errorMessage, true);
          } else {
            throw errorMessage;
          }
        }
        if (newidx == -1) {
          this.lastPoint = idx;
          break;
        }
        idx = newidx;
      }
      this.lastPoint = idx;

      // exit point
      if (idx == length && !this.ended && this.endFunction) {
        this.endFunction();
        this.ended = true;
      }
    } catch (e) {
      console.log(e);
      throw e;
    }
  }

  findLineNumber(idx) {
    let i = idx;
    while (i >= 0) {
      let statement = this.parser.statements[i];
      let type = statement.getType();
      if (type === "LINENUMBER") {
        return statement.text;
      }
      i--;
    }
    return 0;
  }

  interpret() {
    this.data = [];
    this.dataPointer = 0;
    this.variables = [];
    for (let i = 0; i < this.parser.statements.length; i++) {
      let statement = this.parser.statements[i];
      if (statement.getType() == "DATA") {
        let dataCount = statement.children.length;
        for (let j = 0; j < dataCount; j++) {
          this.data.push(statement.children[j]);
        }
      }
    }

    this.lastInputVar = 0;
    try {
      this.run(0);  // 0th statement index
    } catch (e) {
      console.log(e)
    }
  }

  setParser(p) {
    this.parser = p;
  }

  forceEnd() {
    if (!this.ended) {
      this.end_statement();
    }
  }

  /* --- Statement methods ---*/

  assignment_statement(self, idx) {
    let statement = this.parser.statements[idx];
    let variable = statement.children[0];
    let expr = statement.children[1];
    let val = this.evalExpr(expr);
    this.setValue(variable, val);
    return idx + 1;
  }

  clear_statement(self, idx) {
    this.variables = [];
    return idx + 1;
  }

  cls_statement(self, idx) {
    if (this.clsFunction) {
      this.clsFunction();
    }
    return idx + 1;
  }

  data_statement(self, idx) {
    return idx + 1;
  }

  def_statement(self, idx) {
    let statement = this.parser.statements[idx];
    let name = statement.children[0].getText();
    // add to defInfo array (if does not exist)
    if (typeof this.defInfo[name] === "undefined") {
      let param = statement.children[1];
      let func = statement.children[2];
      this.defInfo[name] = {
        param: param,
        func: func
      };
    }
    return idx + 1
  }

  dim_statement(self, idx) {
    let statement = this.parser.statements[idx];
    for (let i = 0; i < statement.children.length; i++) {
      let currentVar = statement.children[i];
      let name = currentVar.getText();
      name += "[]";
      if (typeof this.variables[name] === "undefined") {
        let v = new Variable(name);
        let bounds = this.getArrayIndices(currentVar);
        v.setBounds(bounds);
        this.variables[name] = v;
      }
    }
    return idx + 1;
  }

  // does nothing just steps over the node
  dummy_statement(self, idx) {
    return idx + 1;
  }

  end_statement(self, idx) {
    if (this.endFunction) {
      this.endFunction();
    }
    this.ended = true;
    return this.parser ? this.parser.statements.length + 1 : 0;
  }

  for_statement(self, idx) {
    let statement = this.parser.statements[idx];
    let loopVar = statement.children[0].getText();
    let start = statement.children[1];
    let startVal = this.evalExpr(start);
    if (typeof startVal !== "number") {
      throw "FOR expression is not numeric";
    }
    let end = statement.children[2];
    let endVal = this.evalExpr(end);
    if (typeof endVal !== "number") {
      throw "TO expression is not numeric";
    }
    let stepVal = 1;
    if (statement.children.length == 4) {
      let step = statement.children[3];
      stepVal = this.evalExpr(step);
    }
    // find the next
    let next = this.findNext(idx, loopVar);
    this.setNumericValue(loopVar, startVal);
    let info = {
      "lvar": loopVar,
      "limit": endVal,
      "step": stepVal,
      "body": idx + 1
    };
    this.forInfo[idx] = info;
    this.forStack.push(idx);
    return idx + 1; // start to execute loop
  }

  gosub_statement(self, idx) {
    this.checkBreak();

    let statement = this.parser.statements[idx];
    let label = statement.getText();
    this.gosubStack.push(idx + 1);
    if (this.gosubStack.length > MAX_GOSUB) {
      throw "GOSUB stack exceeded";
    }
    return this.findLabel(label);
  }

  goto_statement(self, idx) {
    this.checkBreak();

    let statement = this.parser.statements[idx];
    let label = statement.getText();
    return this.findLabel(label);
  }

  width_statement(self, idx) {
    // do nothing
    return idx + 1;
  }

  if_statement(self, idx) {
    let statement = this.parser.statements[idx];
    let expr = statement.children[0];
    let val = this.evalExpr(expr);
    if (val) {
      let thenpart = statement.children[1];
      return this.findLabel(thenpart.getText());
    } else {
      let elsepart = statement.children[2];
      return this.findLabel(elsepart.getText());
    }
  }

  input_statement(self, idx) {
    this.printPos = 0;

    let statement = this.parser.statements[idx];
    let count = statement.children.length;
    let prompt = statement.children[0].text;

    if (this.printFunction && this.lastInputVar == 0) {
      this.printFunction(prompt, false);
      this.lastInputVar = 1;
    }

    for (let i = this.lastInputVar; i < count; i++) {
      let variable = statement.children[i];
      let name = variable.text;
      let val = null;
      if (this.inputStack.length > 0) {
        this.setValue(variable, this.inputStack.shift());
        continue;
      }
      if (name[name.length - 1] == "$") {
        if (this.stringInputFunction) {
          val = this.stringInputFunction(prompt, count - 1);
        } else {
          throw "String input function not defined";
        }
      } else {
        if (this.numberInputFunction) {
          val = this.numberInputFunction(prompt, count - 1);
        } else {
          throw "Number input function not defined";
        }
      }
      if (val == null) {
        this.lastInputVar = i;
        return -1; // pause until we get value for input
      }
      this.setValue(variable, val);
    }
    if (this.printFunction) {
      this.printFunction("", true);
    }
    this.lastInputVar = 0;
    return idx + 1;
  }

  checkBreak() {
    if (this.inkeyFunction && this.breakFunction) {
      var codeKey = this.inkeyFunction(0);
      if (codeKey == 3) this.breakFunction();
    }
  }

  next_statement(self, idx) {
    if (this.forStack.length == 0) {
      throw "NEXT without FOR";
    }

    this.checkBreak();

    let statement = this.parser.statements[idx];
    let nidx = this.forStack[this.forStack.length - 1];
    let info = this.forInfo[nidx];
    let loopVar = info.lvar;
    // NEXT statement without loopvar
    if (statement.children.length == 0) {
      let val = this.getNumericValue(loopVar);
      val += info.step;
      if (info.step >= 0) {
        if (val > info.limit) {
          this.forStack.pop();
          return idx + 1;
        }
      } else {
        if (val < info.limit) {
          this.forStack.pop();
          return idx + 1;
        }
      }
      this.setNumericValue(loopVar, val);
      return info.body;
    }

    // iterate through loopvars (for NEXT I,J,K... format is possible)
    let count = statement.children.length;
    for (let j = 0; j < count; j++) {
      let nextVar = statement.children[j].text;

      // looking for unexpected loop variables, remove them from the stack
      // handles jumping out of loop
      while (this.forStack.length > 0 && nextVar !== loopVar) {
        this.forStack.pop();
        if (this.forStack.length > 0) {
          nidx = this.forStack[this.forStack.length - 1];
          info = this.forInfo[nidx];
          loopVar = info.lvar;
        } else {
          throw "Out of FOR stack";
        }
      }

      // expected loop variable
      if (nextVar === loopVar) {
        let val = this.getNumericValue(loopVar);
        val += info.step;
        let done = false;
        if (info.step >= 0) {
          if (val > info.limit)
            done = true;
        } else {
          if (val < info.limit)
            done = true;
        }
        if (done) {
          this.forStack.pop();
          if (j == count - 1) {
            return idx + 1;
          }
          if (this.forStack.length == 0) {
            throw "NEXT without FOR";
          }
          nidx = this.forStack[this.forStack.length - 1];
          info = this.forInfo[nidx];
          loopVar = info.lvar;
          continue;
        }
        this.setNumericValue(loopVar, val);
        return info.body;
      } else {
        throw "Expected NEXT " + loopVar + " got NEXT " + nextVar;
      }
    }
    return nidx;
  }

  on_statement(self, idx) {
    let statement = this.parser.statements[idx];
    let expr = statement.children[0];
    let val = this.evalExpr(expr);

    checkBreak();

    if (val) {
      let gpart = statement.children[1];  // can be GOTO or GOSUB
      let type = gpart.getType();
      let count = gpart.children.length;
      for (let i = 0; i < count; i++) {
        let label = gpart.children[i].getText();
        if (val == i + 1) {
          if (type === "GOTO") {
            return this.findLabel(label);
          } if (type === "GOSUB") {
            this.gosubStack.push(idx + 1);
            if (this.gosubStack.length > MAX_GOSUB) {
              throw "GOSUB stack exceeded";
            }
            return this.findLabel(label);
          }
        }
      }
    }
    return idx + 1;
  }

  print_statement(self, idx) {
    let statement = this.parser.statements[idx];
    let count = statement.children.length;
    let result = "";
    let currentPos = this.printPos;
    let prevPos = this.printPos;

    for (let i = 0; i < count; i++) {
      let child = statement.children[i];
      if (child.getType() === "EXPRESSION") {
        let val = this.evalExpr(child);
        if (child.children.length > 0 && child.children[0].getText() == "TAB") {
          currentPos = val - currentPos - 1;
          if (currentPos > 0) {
            result += TAB_CHARACTER.repeat(currentPos);
          }
        } else {
          // add spaces around numbers
          if (("" + val).trim() != "" && (typeof val === "number") && !isNaN(val)) {
            // truncate decimals
            if (TRUNCATE_DECIMALS) {
              val = Utils.toFixed(val, MAX_DECIMALS);
            }
            val = val + SPACE_CHARACTER;
            // no space added if no result yet (1st col), or the result ends with space or tab
            if (result.length > 0 && !result.endsWith(SPACE_CHARACTER) && !result.endsWith(TAB_CHARACTER)) {
              val = SPACE_CHARACTER + val;
            }
          }
          result += val;
        }
        currentPos = prevPos + result.length;
        this.printPos = currentPos;
      }
      // delimiter increases (rounds up) the tabulator position to the next tab size
      else if (child.getType() === "DELIMITER" && child.getText() === ",") {
        if (currentPos < 1) {
          currentPos = DEFAULT_TAB_SIZE;
        } else {
          if (currentPos % DEFAULT_TAB_SIZE == 0) {
            currentPos++;
          }
          currentPos = Math.ceil(currentPos / DEFAULT_TAB_SIZE) * DEFAULT_TAB_SIZE; // round to the next
        }
        if (currentPos > 0) {
          result += TAB_CHARACTER.repeat(currentPos - result.length);
        }
        this.printPos = currentPos;
      }
    }

    let eol = true;
    if (count > 0 && statement.children[count - 1].text == ";") {
      eol = false;
    } else {
      this.printPos = 0;
    }

    if (this.printFunction) {
      this.printFunction(result, eol);
    }

    return idx + 1;
  }

  randomize_statement(self, idx) {
    let statement = this.parser.statements[idx];
    let val = this.evalExpr(statement.children[0]);
    if (Utils.isNumber(val)) {
      this.random.setSeed(val);
    } else {
      throw "RANDOMIZE expects number as argument";
    }
    return idx + 1;
  }

  sleep_statement(self, idx) {
    let statement = this.parser.statements[idx];
    let ms = this.evalExpr(statement.children[0]);
    if (Utils.isNumber(ms)) {
       if (this.sleepFunction) {
         this.sleepFunction(ms);
       } else {
         const start = Date.now();
         let now = start;
         while (now - start < ms) {
           now = Date.now();
         }
       }
    } else {
      throw "SLEEP expects number as argument";
    }
    return idx + 1;
  }

  read_statement(self, idx) {
    let statement = this.parser.statements[idx];
    let count = statement.children.length;
    for (let i = 0; i < count; i++) {
      let variable = statement.children[i];
      if (this.dataPointer > this.data.length) {
        throw "READ without DATA";
      }
      let value = this.data[this.dataPointer++].text;
      this.setValue(variable, value);
    }
    return idx + 1;
  }

  restore_statement(self, idx) {
    this.dataPointer = 0;
    return idx + 1;
  }

  return_statement(self, idx) {
    if (this.gosubStack.length == 0) {
      throw "RETURN without GOSUB";
    }
    return this.gosubStack.pop();
  }

  wend_statement(self, idx) {
    this.checkBreak();

    if (this.whileStack.length == 0) {
      throw "WEND without WHILE";
    }
    return this.whileStack.pop();
  }

  while_statement(self, idx) {
    let statement = this.parser.statements[idx];
    let expr = statement.children[0];
    let val = this.evalExpr(expr);
    if (typeof val !== "number" && typeof val !== "boolean") {
      throw "WHILE expression is not boolean";
    }
    if (val) {
      this.whileStack.push(idx);
      return idx + 1;
    } else {
      let next = this.findWend(idx);
      return next + 1;
    }
  }

}
