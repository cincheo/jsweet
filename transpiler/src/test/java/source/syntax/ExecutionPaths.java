/* 
 * JSweet - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package source.syntax;

public class ExecutionPaths {
	public int ifElseReturns(boolean b) {
		if (b) {
			return 0;
		} else {
			return 1;
		}
	}

	public int ifElseNoReturns(boolean b) {
		if (b) {
			System.out.println("if");
		} else {
			System.out.println("else");
		}

		System.out.println("after ifelse");

		return 3;
	}

	public int ifReturnInElse(boolean b) {
		if (b) {
			System.out.println("if");
		} else {
			System.out.println("else");
			// return in else
			return 1;
		}

		System.out.println("after ifelse (else path shouldn't contain this)");

		return 3;
	}

	public int ifElseDeepWithReturnsForSomePaths(boolean b, boolean b2, boolean b3) {
		if (b) {
			System.out.println("ifb");
		} else if (b2) {
			System.out.println("ifb2");

			if (b3) {
				System.out.println("ifb3");
				return 9;
			}

			if (!b3) {
				System.out.println("if!b3");
			}

			System.out.println("end elseb2");
		} else {
			System.out.println("else");
		}

		System.out.println("after ifelse");

		return 3;
	}

	public String tryFinally() {
		try {
		} finally {
			System.out.println("ok");
		}
		return "ok";
	}

	public String tryWithCatchesAndFinally() {
		try {
			System.out.println("try");
		} catch (RuntimeException e) {
			System.out.println("e1");
		} catch (Exception e) {
			return "EXIT";
		} finally {
			System.out.println("finally");
		}
		return "ok";
	}

	public void switchWithTryCatch(String switchVal) {
		switch (switchVal) {
		case "a":
		case "b":
			System.out.println("case ab");
			break;

		case "c":
			System.out.println("case c");
			return;

		default:
			try {
				System.out.println("case default");
			} catch (Throwable t) {
				System.out.println("catch");
				return;
			}
			break;
		}

		System.out.println("after switch");

		return;
	}

	public int forIfElse(boolean b) {
		for (int i = 0; i < 10; i++) {
			if (b) {
				System.out.println("if");
			} else {
				System.out.println("else");
				return 1;
			}
		}

		System.out.println("after for & if");

		return 3;
	}

	public int forIfBreak(boolean b) {
		for (int i = 0; i < 10; i++) {
			if (b) {
				break;
			}
		}

		System.out.println("after for & if");

		return 3;
	}

	@SuppressWarnings("all")
	public void testPerfsIfs() {

		System.out.println("testPerfsIfs");
		
		if (true) {
			if (false) {
				if (true) {
					System.out.println("hello");
				}
			}
			if (true) {
				if (true) {
					System.out.println("hello");
				}
			}
			if (false) {
				if (true) {
					System.out.println("hello");
				}
			}
		}
		if (true) {
			if (false) {
				if (true) {
					System.out.println("hello");
				}
			}
			if (true) {
				if (true) {
					System.out.println("hello");
				}
			}
			if (false) {
				if (true) {
					System.out.println("hello");
				}
			}
		}

		if (true) {
			if (false) {
				if (true) {
					System.out.println("hello");
				}
			}
			if (true) {
				if (true) {
					System.out.println("hello");
				}
			}
			if (false) {
				if (true) {
					System.out.println("hello");
				}
			}
		}
		if (true) {
			if (false) {
				if (true) {
					System.out.println("hello");
				}
			}
			if (true) {
				if (true) {
					System.out.println("hello");
				}
			}
			if (false) {
				if (true) {
					System.out.println("hello");
				}
			}
		}
	}

}