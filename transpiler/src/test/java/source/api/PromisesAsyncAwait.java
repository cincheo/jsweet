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
package source.api;

import static def.dom.Globals.setTimeout;
import static jsweet.util.Lang.$export;
import static jsweet.util.Lang.async;
import static jsweet.util.Lang.asyncReturn;
import static jsweet.util.Lang.await;
import static jsweet.util.Lang.function;

import java.util.function.Consumer;

import def.js.Error;
import def.js.Promise;
import jsweet.lang.Async;

class PromiseResult {
	String content;
}

public class PromisesAsyncAwait {

	public static final int WAIT_BETWEEN_STEPS_MS = 150;

	public static void main(String[] args) {
		new PromisesAsyncAwait().start();
	}

	@Async
	void start() {

		await(testPlainAsyncAwait());

		testPromises();
	}

	@Async
	private Promise<Void> testPlainAsyncAwait() {
		try {
			$export("t0", System.currentTimeMillis());
			await(delay(WAIT_BETWEEN_STEPS_MS));

			int result = 42;
			System.out.println("result 1 is " + result);
			$export("r1", result);
			$export("t1", System.currentTimeMillis());

			PromiseResult result2 = await(getAnotherAnswer("What is the age of the captain"));

			System.out.println("result 2 is " + result2);
			$export("r2", result2.content);
			$export("t2", System.currentTimeMillis());

			throw new Error("supermessage");
		} catch (Error error) {

			System.out.println("error is " + error);
			$export("e", error.getMessage());
		}

		return null;
	}

	@Async
	private void testPromises() {
		def.js.Function getResultAsync = async(function(() -> {
			await(delay(WAIT_BETWEEN_STEPS_MS));
			return asyncReturn(42);
		}));

		$export("2_t0", System.currentTimeMillis());
		Promise<Integer> resultPromise = getResultAsync.$apply();
		await(resultPromise //
				.thenAsync(result -> {

					System.out.println("result 1 is " + result);
					$export("2_r1", result);
					$export("2_t1", System.currentTimeMillis());

					return getAnotherAnswer("What is the age of the captain");
				}) //
				.then((PromiseResult result) -> {

					System.out.println("result 2 is " + result);
					$export("2_r2", result.content);
					$export("2_t2", System.currentTimeMillis());

					throw new Error("supermessage");
				}) //
				.Catch(error -> {

					System.out.println("error is " + error);
					$export("2_e", ((Error) error).getMessage());
				}));
	}

	@Async
	private Promise<PromiseResult> getAnotherAnswer(String baseQuestion) {
		await(delay(WAIT_BETWEEN_STEPS_MS));

		return asyncReturn(new PromiseResult() {
			{
				content = "my answer";
			}
		});
	}

	private Promise<Void> delay(int millis) {
		return new Promise<Void>((Consumer<Void> resolve, Consumer<Object> reject) -> {
			setTimeout(resolve, millis);
		});
	}
}