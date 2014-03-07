/*******************************************************************************
 * Copyright (c) 2013, 2014 Sylvain Lézier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sylvain Lézier - initial implementation
 *******************************************************************************/
package sft.decorators;


import sft.DefaultConfiguration;
import sft.UseCase;
import sft.result.UseCaseResult;

public interface Decorator {

    Decorator withParameters(String... parameters);

    Decorator withConfiguration(DefaultConfiguration configuration);

    public String applyOnUseCase(UseCaseResult useCaseResult, String result);

    String applyOnScenario(String result);

    String applyOnFixture(String result);

    boolean comply(Decorator other);
}