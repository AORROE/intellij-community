package de.plushnikov.intellij.plugin.processor.clazz.builder;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import de.plushnikov.intellij.plugin.lombokconfig.ConfigDiscovery;
import de.plushnikov.intellij.plugin.problem.ProblemBuilder;
import de.plushnikov.intellij.plugin.processor.handler.SuperBuilderHandler;
import de.plushnikov.intellij.plugin.settings.ProjectSettings;
import de.plushnikov.intellij.plugin.util.PsiClassUtil;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Inspect and validate @SuperBuilder lombok annotation on a class
 * Creates inner classes for a @SuperBuilder pattern
 *
 * @author Michail Plushnikov
 */
public class SuperBuilderClassProcessor extends BuilderClassProcessor {

  private final SuperBuilderHandler superBuilderHandler;

  public SuperBuilderClassProcessor(@NotNull ConfigDiscovery configDiscovery, @NotNull SuperBuilderHandler builderHandler) {
    super(configDiscovery, builderHandler, SuperBuilder.class);
    this.superBuilderHandler = builderHandler;
  }

  @Override
  public boolean isEnabled(@NotNull PropertiesComponent propertiesComponent) {
    return ProjectSettings.isEnabled(propertiesComponent, ProjectSettings.IS_SUPER_BUILDER_ENABLED);
  }

  @Override
  protected boolean validate(@NotNull PsiAnnotation psiAnnotation, @NotNull PsiClass psiClass, @NotNull ProblemBuilder builder) {
    return superBuilderHandler.validate(psiClass, psiAnnotation, builder);
  }

  protected void generatePsiElements(@NotNull PsiClass psiClass, @NotNull PsiAnnotation psiAnnotation, @NotNull List<? super PsiElement> target) {
    final String builderClassName = superBuilderHandler.getBuilderClassName(psiClass);
    if (!PsiClassUtil.getInnerClassInternByName(psiClass, builderClassName).isPresent()) {
      target.add(superBuilderHandler.createBuilderClass(psiClass, psiAnnotation));
    }

    final String builderImplClassName = superBuilderHandler.getBuilderImplClassName(psiClass);
    if (!PsiClassUtil.getInnerClassInternByName(psiClass, builderImplClassName).isPresent()) {
      target.add(superBuilderHandler.createBuilderImplClass(psiClass, psiAnnotation));
    }
  }
}
