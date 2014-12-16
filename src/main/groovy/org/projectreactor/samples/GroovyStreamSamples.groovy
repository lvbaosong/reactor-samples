/*
 * Copyright (c) 2011-2015 Pivotal Software Inc., Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.projectreactor.samples

import reactor.Environment
import reactor.rx.Promise
import reactor.rx.Promises
import reactor.rx.Stream
import reactor.rx.Streams

final ENV = new Environment()

def simpleStream = {
	// Deferred is the publisher, Stream the consumer
	def deferred = Streams.<String> broadcast()
			.env(ENV)
			.dispatcher(Environment.SHARED)
			.get()

	Stream<String> stream = deferred

	// Consume values passing through the Stream
	stream.consume { println "Consumed String $it" }

	// Publish a value
	deferred << "Hello World!"
}

def transformValues = {
	// Deferred is the publisher, Stream the consumer
	def deferred = Streams.<String> broadcast()
			.env(ENV)
			.dispatcher(Environment.SHARED)
			.get()
	Stream stream = deferred

	// Transform values passing through the Stream
	def transformation = stream | { String data -> data.toUpperCase() }
	transformation << { println "UC String $it" }

	// Publish a value
	deferred << "Hello World!"
}

def filterValues = {
	// Deferred is the publisher, Stream the consumer
	def deferred = Streams.<String> broadcast()
			.env(ENV)
			.dispatcher(Environment.SHARED)
			.get()

	Stream stream = deferred

	// Filter values passing through the Stream
	stream.filter { String data -> data.startsWith("Hello") } << { println "Filtered String $it" }

	// Publish a value
	deferred << "Hello World!" << "Goodbye World!"
}

simpleStream()
transformValues()
filterValues()

def rand = new Random()

def p1 = Promise.from{ sleep(rand.nextInt(500)); 'Jon' } env(ENV) dispatcher(Environment.THREAD_POOL) get()
def p2 = Promise.from{ sleep(rand.nextInt(500)); 'Stephane' } env(ENV) dispatcher(Environment.THREAD_POOL) get()
def p3 = Promise.from{ sleep(rand.nextInt(1000)); 'Chuck Norris' } env(ENV) dispatcher(Environment.THREAD_POOL) get()

p1.get()
p2.get()
p3.get()

Promises.any(p1, p2, p3).onSuccess { println "$it won !"} await()

ENV.shutdown()