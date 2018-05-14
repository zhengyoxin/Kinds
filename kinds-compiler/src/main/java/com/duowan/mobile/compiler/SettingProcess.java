package com.duowan.mobile.compiler;

import com.duowan.mobile.main.annotation.BooleanKindSetting;
import com.duowan.mobile.main.annotation.BooleanKindValue;
import com.duowan.mobile.main.annotation.IntKindSetting;
import com.duowan.mobile.main.annotation.IntKindValue;
import com.duowan.mobile.main.annotation.KindSetting;
import com.duowan.mobile.main.annotation.StringKindSetting;
import com.duowan.mobile.main.annotation.StringKindValue;
import com.duowan.mobile.main.kinds.Kind;
import com.duowan.mobile.main.kinds.KindMapSyringe;
import com.duowan.mobile.main.kinds.KindStorage;
import com.duowan.mobile.main.kinds.wrapper.BooleanKindWrapper;
import com.duowan.mobile.main.kinds.wrapper.IntKindWrapper;
import com.duowan.mobile.main.kinds.wrapper.KindWrapper;
import com.duowan.mobile.main.kinds.wrapper.StringKindWrapper;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * Created by ericwu on 2017/9/27.
 */
@AutoService(Processor.class)
public class SettingProcess extends AbstractProcessor {

    private static final String PACKAGE = "com.duowan.mobile.setting";
    private static final String TAG = "SettingProcess";

    private static final String METHOD_CREATE_INSTANCE = "createInstance";
    private static final String METHOD_INITIALIZE_INDEX = "initializeIndex";

    private static final List<Class<? extends Annotation>> SETTINGS = Arrays.asList(//
            BooleanKindSetting.class,
            StringKindSetting.class,
            IntKindSetting.class
    );

    private Messager messager;

    private String moduleName;
    private String projectName;

    // Options of processor
    public static final String KEY_MODULE_NAME = "FeatureModuleName";
    public static final String KEY_PROJECT_NAME = "FeatureProjectName";
    private HashMap<String, KindUnit> map = new HashMap<>();

    /**
     * @return 指定哪些注解应该被注解处理器注册
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(BooleanKindSetting.class.getCanonicalName());
        types.add(BooleanKindValue.class.getCanonicalName());
        types.add(StringKindSetting.class.getCanonicalName());
        types.add(StringKindValue.class.getCanonicalName());
        types.add(IntKindSetting.class.getCanonicalName());
        types.add(IntKindValue.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        messager = processingEnv.getMessager();   // Package the log utils.

        // Attempt to get user configuration [moduleName]
        Map<String, String> options = processingEnv.getOptions();
        if (options != null && !options.isEmpty()) {
            moduleName = options.get(KEY_MODULE_NAME);
            projectName = options.get(KEY_PROJECT_NAME);
        }

        checkOptionsNonNull(moduleName, KEY_MODULE_NAME);
        checkOptionsNonNull(projectName, KEY_PROJECT_NAME);

        for (Class<? extends Annotation> setting : SETTINGS) {
            messager.printMessage(Diagnostic.Kind.NOTE, "[SETTINGS] setting = " + setting);
            findAndParseListener(roundEnvironment, setting);
        }

        generateFeatureWrapper();

        generateFeatureMapSyringe();
        return false;
    }

    private void checkOptionsNonNull(String optionsName, String optionsConstant) {

        if (optionsName != null && !optionsName.isEmpty()) {
            optionsName = optionsName.replaceAll("[^0-9a-zA-Z_]+", "");
            messager.printMessage(Diagnostic.Kind.NOTE, "The user has configuration the " + optionsConstant + ", it was [" + optionsName + "]");
        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, "These no " + optionsConstant + ", at 'build.gradle', like :\n" +
                    "apt {\n" +
                    "    arguments {\n" +
                    "        moduleName project.getName();\n" +
                    "        projectName rootProject.getName();\n" +
                    "    }\n" +
                    "}\n");
            throw new RuntimeException("Kinds::Compiler >>> No " + optionsConstant + " for more information, look at gradle log.");
        }
    }

    private void findAndParseListener(RoundEnvironment env, Class<? extends Annotation> annotationClass) {
        for (Element element : env.getElementsAnnotatedWith(annotationClass)) {
            try {
                messager.printMessage(Diagnostic.Kind.NOTE, "[findAndParseListener] class:" + element);
                parseSettingAnnotation(annotationClass, element);
            } catch (Throwable e) {
                messager.printMessage(Diagnostic.Kind.NOTE, "[findAndParseListener] error");
                e.printStackTrace();
            }
        }
    }

    private void parseSettingAnnotation(Class<? extends Annotation> annotationClass, Element element) throws Exception {
        if (element.getKind() == ElementKind.CLASS || element.getKind() == ElementKind.INTERFACE) {
            Annotation annotation = element.getAnnotation(annotationClass);
            Method settingPath = annotationClass.getDeclaredMethod("settingPath");
            if (settingPath.getReturnType() != String.class) {
                throw new IllegalStateException(
                        String.format("@%s annotation settingPath() type not String.", annotationClass));
            }
            String path = (String) settingPath.invoke(annotation);
            String group = extractGroup(projectName);
            messager.printMessage(Diagnostic.Kind.NOTE, "[parseSettingAnnotation] path=" + path + ",  group=" + group);

            Method aliasMethod = annotationClass.getDeclaredMethod("alias");
            if (aliasMethod.getReturnType() != String.class) {
                throw new IllegalStateException(
                        String.format("@%s annotation alias() type not String.", annotationClass));
            }
            String alias = (String) aliasMethod.invoke(annotation);

            Method defValueMethod = annotationClass.getDeclaredMethod("defValue");
            Object defValue = defValueMethod.invoke(annotation);

            KindUnit unit;
            if (map.get(path) == null) {
                unit = new KindUnit();
                unit.setPath(path);
                unit.setGroup(group);
                unit.setAlias(alias);
                unit.setDefValue(defValue);
                map.put(path, unit);
            } else {
                unit = map.get(path);
            }
            KindSetting kindSetting = getSettingAnnotation(element);
            if (kindSetting != null) {
                messager.printMessage(Diagnostic.Kind.NOTE, "find out parent class:" + element);
                unit.setParent(element, getSettingType(kindSetting));
            } else {
                messager.printMessage(Diagnostic.Kind.NOTE, "find out child class:" + element);
                unit.addChild(element);
            }
        }
    }

    private void generateFeatureMapSyringe() {
        String className = "KindMapSyringe$$" + projectName + "$$" + moduleName;
        TypeSpec.Builder syringeBuilder = TypeSpec.classBuilder(className)
                .addSuperinterface(KindMapSyringe.class)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        // Class<? extends Kind>
        ParameterizedTypeName fanxing = ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(Kind.class));
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class), fanxing);

        // Class<? entends KindWrapper>
        ParameterizedTypeName fanxing2 = ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(KindWrapper.class));
        ParameterizedTypeName parameter2 = ParameterizedTypeName.get(ClassName.get(Map.class), fanxing, fanxing2);
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("injectFeatureInto")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(parameterizedTypeName, "featureMap")
                .addParameter(parameter2, "wrapperMap");

        Set<Map.Entry<String, KindUnit>> set = map.entrySet();
        CodeBlock.Builder codeBlock = CodeBlock.builder();
        for (Map.Entry<String, KindUnit> stringFeatureUnitEntry : set) {
            KindUnit unit = stringFeatureUnitEntry.getValue();
            TypeName implClass = ClassName.get(PACKAGE, unit.getWrapperClzName());
            codeBlock.addStatement("featureMap.put($S, $T.class)", unit.getPath(), unit.getParent());
            codeBlock.addStatement("wrapperMap.put($T.class, $T.class)", unit.getParent(), implClass);
        }

        methodBuilder.addCode(codeBlock.build());

        syringeBuilder.addMethod(methodBuilder.build());

        JavaFile javaFile = JavaFile.builder(PACKAGE, syringeBuilder.build())
                .build();
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    private void generateFeatureWrapper() {
        Set<Map.Entry<String, KindUnit>> set = map.entrySet();
        for (Map.Entry<String, KindUnit> unitEntry : set) {
            KindUnit unit = unitEntry.getValue();
            generateFeature(unit);
        }
    }

    //https://stackoverflow.com/questions/7687829/java-6-annotation-processing-getting-a-class-from-an-annotation
    private TypeMirror getSettingType(KindSetting kindSetting) {
        try {
            kindSetting.value();
        } catch (MirroredTypeException e) {
            return e.getTypeMirror();
        }
        return null;
    }

    private static AnnotationMirror getAnnotationMirror(TypeElement typeElement, Class<?> clazz) {
        String clazzName = clazz.getName();
        for (AnnotationMirror m : typeElement.getAnnotationMirrors()) {
            if (m.getAnnotationType().toString().equals(clazzName)) {
                return m;
            }
        }
        return null;
    }

    private static AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String key) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    //这个写得更长些，方法归整得好些
    private TypeMirror getContainerClass(TypeElement foo) {
        AnnotationMirror am = getAnnotationMirror(foo, KindSetting.class);
        if (am == null) {
            return null;
        }
        AnnotationValue av = getAnnotationValue(am, "value");
        if (av == null) {
            return null;
        } else {
            return (TypeMirror) av.getValue();
        }
    }


    private boolean isSettingElement(Element element) {
        KindSetting kindSetting = getSettingAnnotation(element);
        if (kindSetting != null) {
            return true;
        }
        return false;
    }

    private KindSetting getSettingAnnotation(Element element) {
        List<? extends AnnotationMirror> mirrors = element.getAnnotationMirrors();
        messager.printMessage(Diagnostic.Kind.NOTE, "[getSettingAnnotation] mirrors = " + mirrors);
        for (AnnotationMirror mirror : mirrors) {
            KindSetting kindSetting = mirror.getAnnotationType().asElement().getAnnotation(KindSetting.class);
            if (kindSetting != null) {
                messager.printMessage(Diagnostic.Kind.NOTE, "find out element:" + element);
                return kindSetting;
            }
        }
        return null;
    }

    private void generateFeature(KindUnit unit) {
        if (TypeName.get(unit.getValueType()).equals(TypeName.get(BooleanKindValue.class))) {
            buildBooleanFeature(unit);
        } else if (TypeName.get(unit.getValueType()).equals(TypeName.get(StringKindValue.class))) {
            buildStringFeature(unit);
        } else if (TypeName.get(unit.getValueType()).equals(TypeName.get(IntKindValue.class))) {
            buildIntFeature(unit);
        }
    }

    private void buildStringFeature(KindUnit unit) {
        messager.printMessage(Diagnostic.Kind.NOTE, "[buildStringFeature] start");
        Element element = unit.getParent();
        String className = unit.getWrapperClzName();

        Object defaultValue = unit.getDefValue();
        String storageKey = unit.getPath();
        TypeSpec.Builder stringBuilder = TypeSpec.classBuilder(className)
                .superclass(ParameterizedTypeName.get(
                        ClassName.get(StringKindWrapper.class),
                        TypeName.get(element.asType())))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(KindStorage.class, "storage")
                .addParameter(Class.class, "clz");

        constructor.addStatement("super(storage, $S, $S, clz, $L, $S, $S)", storageKey, defaultValue,
                unit.getChildren() == null ? 0 : unit.getChildren().size(), unit.getAlias(), unit.getGroup());


        stringBuilder.addMethod(constructor.build());

        //#initializeIndex
        MethodSpec.Builder initializeIndexMethod = MethodSpec.methodBuilder(METHOD_INITIALIZE_INDEX)
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .returns(TypeName.VOID);

        //code blocks
        CodeBlock.Builder indexCodeBlock = CodeBlock.builder();

        if (unit.getChildren() != null) {
            int index = 0;
            for (Element typeElement : unit.getChildren()) {
                StringKindValue strValue = typeElement.getAnnotation(StringKindValue.class);
                indexCodeBlock.addStatement("mapIndex($S, $L, $T.class)", strValue.value(), index, typeElement);
                index++;
            }
        }

        initializeIndexMethod.addCode(indexCodeBlock.build());
        stringBuilder.addMethod(initializeIndexMethod.build());

        messager.printMessage(Diagnostic.Kind.NOTE, "[buildStringFeature] before build ");
        JavaFile javaFile = JavaFile.builder(PACKAGE, stringBuilder.build())
                .build();

        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.NOTE, "[buildStringFeature] build.msg = " + e.getMessage());
        }
    }

    private void buildIntFeature(KindUnit unit) {
        messager.printMessage(Diagnostic.Kind.NOTE, "[buildIntFeature] start");
        Element element = unit.getParent();
        String className = unit.getWrapperClzName();

        Object defaultValue = unit.getDefValue();
        String storageKey = unit.getPath();
        TypeSpec.Builder stringBuilder = TypeSpec.classBuilder(className)
                .superclass(ParameterizedTypeName.get(
                        ClassName.get(IntKindWrapper.class),
                        TypeName.get(element.asType())))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(KindStorage.class, "storage")
                .addParameter(Class.class, "clz");

        constructor.addStatement("super(storage, $S, $L, clz, $L, $S, $S)", storageKey, defaultValue,
                unit.getChildren() == null ? 0 : unit.getChildren().size(), unit.getAlias(), unit.getGroup());


        stringBuilder.addMethod(constructor.build());

        //#initializeIndex
        MethodSpec.Builder initializeIndexMethod = MethodSpec.methodBuilder(METHOD_INITIALIZE_INDEX)
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .returns(TypeName.VOID);

        //code blocks
        CodeBlock.Builder indexCodeBlock = CodeBlock.builder();
        messager.printMessage(Diagnostic.Kind.NOTE, "[buildIntFeature] unit.getChildren() = " + unit.getChildren());
        if (unit.getChildren() != null) {
            int index = 0;
            for (Element typeElement : unit.getChildren()) {
                IntKindValue intKindValue = typeElement.getAnnotation(IntKindValue.class);
                indexCodeBlock.addStatement("mapIndex($L, $L, $T.class)", intKindValue.value(), index, typeElement);
                index++;
            }
        }

        initializeIndexMethod.addCode(indexCodeBlock.build());
        stringBuilder.addMethod(initializeIndexMethod.build());

        messager.printMessage(Diagnostic.Kind.NOTE, "[buildIntFeature] before build ");
        JavaFile javaFile = JavaFile.builder(PACKAGE, stringBuilder.build())
                .build();

        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.NOTE, "[buildIntFeature] build.msg = " + e.getMessage());
        }
    }

    private void buildBooleanFeature(KindUnit unit) {
        messager.printMessage(Diagnostic.Kind.NOTE, "[buildBooleanFeature] start");
        Element element = unit.getParent();
        String className = unit.getWrapperClzName();

        Object defaultValue = unit.getDefValue();
        String storageKey = unit.getPath();
        TypeSpec.Builder booleanBuilder = TypeSpec.classBuilder(className)
                .superclass(ParameterizedTypeName.get(
                        ClassName.get(BooleanKindWrapper.class),
                        TypeName.get(element.asType())))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(KindStorage.class, "storage")
                .addParameter(Class.class, "clz")
                .addStatement("super(storage, $S, $L, clz, $S, $S, $L)",
                        storageKey, defaultValue, unit.getAlias(), unit.getGroup(), false);
        booleanBuilder.addMethod(constructor.build());

        //#createInstance
        MethodSpec.Builder createInstanceMethod = MethodSpec.methodBuilder(METHOD_CREATE_INSTANCE)
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .returns(ClassName.get(element.asType()))
                .addParameter(TypeName.BOOLEAN, "value");

        CodeBlock.Builder codeBlock = CodeBlock.builder();

        ArrayList<Element> featureImpls = unit.getChildren();
        if (featureImpls != null) {
            TypeElement trueElement = null;
            TypeElement falseElement = null;

            for (Element impl : featureImpls) {
                TypeElement typeElement = (TypeElement) impl;
                BooleanKindValue booleanKindValue = typeElement.getAnnotation(BooleanKindValue.class);
                if (booleanKindValue.value()) {
                    trueElement = typeElement;
                } else {
                    falseElement = typeElement;
                }
            }

            if (trueElement != null) {
                codeBlock.beginControlFlow("if (value)");
                codeBlock.addStatement("return new $T()", trueElement);
            }
            if (falseElement != null) {
                if (trueElement != null) {
                    codeBlock.nextControlFlow("else");
                } else {
                    codeBlock.beginControlFlow("if (!value)");
                }

                codeBlock.addStatement("return new $T()", falseElement);
                codeBlock.endControlFlow();
            }

        } else {
            codeBlock.addStatement("return null");
        }
        createInstanceMethod.addCode(codeBlock.build());
        booleanBuilder.addMethod(createInstanceMethod.build());

        messager.printMessage(Diagnostic.Kind.NOTE, "[buildBooleanFeature] end build");
        JavaFile javaFile = JavaFile.builder(PACKAGE, booleanBuilder.build())
                .build();

        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Extract the default group from path.
     */
    private String extractGroup(String path) {
        return path;
    }

    /**
     * @return 指定使用的 Java 版本。通常返回 SourceVersion.latestSupported()。
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
