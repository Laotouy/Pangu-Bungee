package cn.mccraft.pangu.bungee.bridge;

import cn.mccraft.pangu.bungee.Bridge;
import cn.mccraft.pangu.bungee.Side;
import cn.mccraft.pangu.bungee.data.Persistence;
import cn.mccraft.pangu.bungee.util.ArrayUtils;
import com.github.mouse0w0.fastreflection.FastReflection;
import com.github.mouse0w0.fastreflection.MethodAccessor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;

public class Solution {
    private Object instance;
    private Method method;
    private Persistence persistence;

    private boolean withPlayer;
    private Type[] actualParameterTypes;
    private String[] actualParameterNames;
    private MethodAccessor methodAccessor;

    private boolean returnAlso;
    private boolean also;
    private Side side;

    public Solution(Object instance, Method method, Persistence persistence, Bridge bridge) throws Exception {
        this.instance = instance;
        this.method = method;
        this.persistence = persistence;
        this.also = bridge.also();
        this.side = bridge.from();
        this.withPlayer = method.getParameterCount() > 0 && ProxiedPlayer.class.isAssignableFrom(method.getParameterTypes()[0]);
        this.actualParameterTypes = withPlayer?(Type[]) ArrayUtils.remove(method.getGenericParameterTypes(), 0):method.getGenericParameterTypes();
        this.actualParameterNames = Arrays.stream(method.getParameters()).map(Parameter::getName).toArray(String[]::new);
        if (withPlayer) {
            this.actualParameterNames = (String[]) ArrayUtils.remove(actualParameterNames, 0);
        }
        this.returnAlso = method.getReturnType() == boolean.class;

        this.methodAccessor = FastReflection.create(method);
    }
    public Solution(Object instance, Method method, Persistence persistence, Bridge bridge,boolean server) throws Exception {
        this.instance = instance;
        this.method = method;
        this.persistence = persistence;
        this.also = bridge.also();
        this.side = bridge.from();
        this.withPlayer = method.getParameterCount() > 0 && ServerInfo.class.isAssignableFrom(method.getParameterTypes()[0]);
        this.actualParameterTypes = withPlayer?(Type[]) ArrayUtils.remove(method.getGenericParameterTypes(), 0):method.getGenericParameterTypes();
        this.actualParameterNames = Arrays.stream(method.getParameters()).map(Parameter::getName).toArray(String[]::new);
        if (withPlayer) {
            this.actualParameterNames = (String[]) ArrayUtils.remove(actualParameterNames, 0);
        }
        this.returnAlso = method.getReturnType() == boolean.class;

        this.methodAccessor = FastReflection.create(method);
    }

    public boolean solve(ProxiedPlayer player, byte[] bytes) throws Exception {
        Object[] objects = persistence.deserialize(actualParameterNames, bytes, actualParameterTypes);
        if (withPlayer) {
            objects = ArrayUtils.add(objects, 0, player);
        }
        Object ret = methodAccessor.invoke(instance, objects);
        if (returnAlso) return (boolean) ret;
        else return false;
    }
    public boolean solve(ServerInfo server, byte[] bytes) throws Exception {
        System.out.println(Arrays.toString(actualParameterNames));
        System.out.println(Arrays.toString(actualParameterTypes));
        Object[] objects = persistence.deserialize(actualParameterNames, bytes, actualParameterTypes);
        if (withPlayer) {
            objects = ArrayUtils.add(objects, 0, server);
        }
        Object ret = methodAccessor.invoke(instance, objects);
        if (returnAlso) return (boolean) ret;
        else return false;
    }



    public boolean isAlso() {
        return also;
    }

    public Side getSide() {
        return side;
    }
}
