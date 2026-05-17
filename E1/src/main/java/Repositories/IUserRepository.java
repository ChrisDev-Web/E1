package Repositories;

import Models.User;

// Interfaces + Repository: une los contratos necesarios para trabajar con User.
public interface IUserRepository extends IRepositoryRegister<User>, IRepositoryLogin<User> {
}
