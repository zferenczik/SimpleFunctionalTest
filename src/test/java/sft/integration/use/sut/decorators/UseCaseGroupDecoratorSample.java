package sft.integration.use.sut.decorators;

import org.junit.Test;
import org.junit.runner.RunWith;
import sft.Decorate;
import sft.FixturesHelper;
import sft.SimpleFunctionalTest;
import sft.decorators.Group;
import sft.integration.use.sut.decorators.subUseCase.OtherSubUseCase;
import sft.integration.use.sut.decorators.subUseCase.SftFixturesHelper;
import sft.integration.use.sut.decorators.subUseCase.SubUseCaseError1;
import sft.integration.use.sut.decorators.subUseCase.SubUseCaseError2;

@RunWith(SimpleFunctionalTest.class)
public class UseCaseGroupDecoratorSample {
    @FixturesHelper
    private SftFixturesHelper sftFixturesHelper = new SftFixturesHelper();

    @Test
    public void scenario(){
        sftFixturesHelper.success();
    }

    @Decorate(decorator = Group.class, parameters ="Error cases")
    public SubUseCaseError1 subUseCaseError1 = new SubUseCaseError1();
    @Decorate(decorator = Group.class, parameters ="Error cases")
    public SubUseCaseError2 subUseCaseError2 = new SubUseCaseError2();
    public OtherSubUseCase otherSubUseCase = new OtherSubUseCase();
}
