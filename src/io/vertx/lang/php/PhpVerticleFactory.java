package io.vertx.lang.php;

import io.vertx.lang.php.streams.impl.InstantWriteStream;

import com.caucho.quercus.QuercusContext;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.NullValue;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.function.AbstractFunction;
import com.caucho.quercus.page.InterpretedPage;
import com.caucho.quercus.page.QuercusPage;
import com.caucho.quercus.parser.QuercusParser;
import com.caucho.quercus.program.QuercusProgram;
import com.caucho.vfs.ReadStream;
import com.caucho.vfs.StdoutStream;
import com.caucho.vfs.StringPath;
import com.caucho.vfs.WriteStream;
import io.vertx.core.*;
import io.vertx.core.spi.VerticleFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;


/**
 * PhpVerticleFactory for Vert.x 3. When deploy in a Java verticle.
 * VerticleFactory fac = new PhpVerticleFactory("php");
 * vertx.registerVerticleFactory(fac);
 * vertx.deployVerticle("test.php", res -> {});
 * @author Leng Sheng Hong
 */
public class PhpVerticleFactory implements VerticleFactory {

    private ClassLoader cl;

    public Vertx vertx;

    public QuercusContext context = null;

    public PhpVerticleFactory(String prefix) {

    }

    @Override
    public boolean blockingCreate() {
        return true;
    }

    @Override
    public void init(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public String prefix() {
        return "php";
    }

    @Override
    public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
        this.cl = classLoader;
        if (context == null) {
            this.initQuercusContext();
        }
        String scriptPath = findScript(verticleName);
        if (scriptPath == null) {
            throw new VertxException(String.format("%s is not a valid PHP verticle.", verticleName));
        }
        return new PhpVerticle(context, scriptPath);
    }

    /**
     * Finds the full path to a PHP script.
     */
    private String findScript(String script) {
        URL filename = cl.getResource(script);
        if (filename != null) {
            File scriptFile = new File(filename.getPath());
            if (scriptFile.exists()) {
                return scriptFile.toPath().toString();
            }
            else {
                return script;
            }
        }
        else {
            File scriptFile = new File(script);
            if (scriptFile.exists()) {
                return scriptFile.toPath().toString();
            }
        }
        return null;
    }

    protected void initQuercusContext() {
        if (context != null) {
            return;
        }

        ClassLoader old = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(cl);

            context = new QuercusContext();
            // Setting PHP's error_reporting to 0 makes Quercus give us more
            // interesting exception messages and thus better error reporting.
            context.setIni("error_reporting", "0");

            //enable utf-8
            context.setUnicodeSemantics(true);

            // Make vertx-php classes available in the PHP code context.
            context.addJavaClass("Vertx\\Buffer", io.vertx.lang.php.buffer.Buffer.class);
            context.addJavaClass("Vertx\\Logger", io.vertx.core.logging.Logger.class);
            context.addJavaClass("Vertx\\ReadStream", io.vertx.lang.php.streams.ReadStream.class);
            context.addJavaClass("Vertx\\WriteStream", io.vertx.lang.php.streams.WriteStream.class);

            context.addJavaClass("Vertx\\Util\\HandlerFactory", io.vertx.lang.php.util.HandlerFactory.class);

            context.init();
            context.start();

            addRequireVertxToContext();

            AbstractFunction func = context.findFunction(context.createString("phpinfo"));

            if (func == null) {
                context = null;
                throw new VertxException("PHP Environment didn't load properly");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Thread.currentThread().setContextClassLoader(old);
        }
    }

    private void addRequireVertxToContext() {
        context.setFunction(context.createString("require_vertx"), new AbstractFunction() {

            private static final long serialVersionUID = 5350698219672910902L;

            @Override
            public Value call(Env env, Value[] args) {
                if (args.length != 1) {
                    throw new IllegalArgumentException("require_vertx: missing Argument path");
                }

                String resourceName = args[0].toString();
                URL resourcePath = cl.getResource(args[0].toString());
                try {
                    String script = String.format("require('%s');", resourcePath.toString());
                    QuercusProgram program = context.parseCode(context.createString(script));
                    program.execute(env);
                } catch (NullPointerException np) {
                    System.out.println(String.format("Could not find Vertx resource '%s''", resourceName));
                    np.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                catch (Exception err) {
                    err.printStackTrace();
                }

                return NullValue.create();
            }
        });
    }

    /**
     * @return the Quercus context for all Verticle created by this factory
     */
    public QuercusContext getQuercusContext() {
        return context;
    }

    /**
     * A PHP Verticle that runs PHP scripts via Quercus.
     */
    private class PhpVerticle extends AbstractVerticle {

        /**
         * The path to the verticle PHP script.
         */
        private final String scriptName;

        private final QuercusContext querContext;

        private WriteStream out;

        private Env globalEnv;

        PhpVerticle(QuercusContext querContext, String script) {
            this.scriptName = script;
            this.querContext = querContext;
        }

        public Vertx getVertx() {
            return this.vertx;
        }

        public void init(Vertx var1, Context var2) {
            this.vertx = var1;
            this.context = var2;
        }

        /**
         * Starts the verticle.
         */
        @Override
        public void start(Future<Void> var1) {
//              context.exceptionHandler(throwable -> {
//                  System.out.println("====== PhpVerticle exceptionHandler: " + throwable.getMessage());
//                  System.out.println("globalEnv.getExceptionHandler() " + globalEnv.getExceptionHandler().toString() + " "  + globalEnv.getExceptionHandler().getCallbackName());
////                  globalEnv.getExceptionHandler().call(globalEnv, globalEnv.wrapJava(throwable, globalEnv.getJavaClassDefinition(throwable.getClass())));
//                  globalEnv.getExceptionHandler().call(globalEnv, globalEnv.wrapJava(new QuercusRuntimeException(throwable)));
//              });
//            vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
//                @Override
//                public void handle(HttpServerRequest httpServerRequest) {
//                     httpServerRequest.response().w
//                }
//            })      ;
//            String classLoaderScript = "spl_autoload_register(function($class) {" +
//                "if(substr($class, 0, 5)==='Vertx'){require_vertx(str_replace('\\\\', '/', $class) . '.php');}" +
//                "});";

            // Evaluate a single line script which includes the verticle
            // script. This ensures that exceptions can be accurately logged
            // because Quercus will record actual file names rather than a
            // generic "eval" name.
//            String script = String.format("<?php " + classLoaderScript + "require '%s'; ?>", this.scriptName);
            String script = String.format("<?php require '%s'; ?>", this.scriptName);

            try (ReadStream reader = (new StringPath(script)).openRead()) {
                try {
                    QuercusProgram program = QuercusParser.parse(querContext, null, reader);
                    QuercusPage page = new InterpretedPage(program);

                    out = new InstantWriteStream(StdoutStream.create());
                    globalEnv = new Env(querContext, page, out, null, null);

                    AbstractFunction abstFunc = new AbstFunc(this.vertx);
                    globalEnv.addFunction("getVertx", abstFunc);

                    AbstractFunction vertFunc = new VerticleFunc(this);
                    globalEnv.addFunction("verticle", vertFunc);

                    AbstractFunction contextExceptionFunc = new ContextExceptionFunc(this.context);
                    globalEnv.addFunction("contextException", contextExceptionFunc);

                    globalEnv.start();

                    program.execute(globalEnv);
                    out.flush();
                    var1.complete();
                } catch (Exception e) {
                    System.out.println("Exception caught when parsed this php file");
                    var1.fail(e);
                }
            } catch (IOException e) {
                var1.fail(new VertxException("Cannot parse PHP verticle: " + this.scriptName));
            } catch (Exception e) {
                var1.fail(e);
            }

        }

        @Override
        public void stop(Future<Void> var1) {
            globalEnv = null;

            if (out != null) {
                try {
                    out.close();
                    var1.complete();
                } catch (IOException e) {
                    var1.fail(e);
                }
                out = null;
            }
        }

    }
}
