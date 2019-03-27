/*
 * Copyright 2016 NAVER Corp.
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
 *
 */

package com.gb.apm.profiler.interceptor.scope;

import com.gb.apm.bootstrap.core.interceptor.scope.InterceptorScope;
import com.gb.apm.profiler.context.scope.PoolObjectFactory;

/**
 * @author Woonduk Kang(emeroad)
 */
public class InterceptorScopeFactory implements PoolObjectFactory<String, InterceptorScope> {
    @Override
    public InterceptorScope create(String scopeName) {
        return new DefaultInterceptorScope(scopeName);
    }
}
