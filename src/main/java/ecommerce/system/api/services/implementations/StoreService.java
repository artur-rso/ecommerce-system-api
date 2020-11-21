package ecommerce.system.api.services.implementations;

import ecommerce.system.api.enums.MessagesEnum;
import ecommerce.system.api.exceptions.InvalidOperationException;
import ecommerce.system.api.models.AddressModel;
import ecommerce.system.api.models.StoreModel;
import ecommerce.system.api.repositories.IStoreRepository;
import ecommerce.system.api.services.IAddressService;
import ecommerce.system.api.services.IAuthenticationService;
import ecommerce.system.api.services.IFileService;
import ecommerce.system.api.services.IStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StoreService implements IStoreService {

    private final IAuthenticationService authenticationService;
    private final IAddressService addressService;
    private final IFileService fileService;
    private final IStoreRepository storeRepository;

    @Autowired
    public StoreService(IAuthenticationService authenticationService, IAddressService addressService, IFileService fileService, IStoreRepository storeRepository) {
        this.authenticationService = authenticationService;
        this.addressService = addressService;
        this.fileService = fileService;
        this.storeRepository = storeRepository;
    }

    @Override
    public void createStore(StoreModel store, int userId) throws InvalidOperationException {

        if (!this.authenticationService.isLoggedUser(userId)) {
            throw new InvalidOperationException(MessagesEnum.UNALLOWED.getMessage());
        }

        store.setCreationDate(LocalDateTime.now());
        store.setLastUpdate(null);
        store.setActive(true);

       int storeId = this.storeRepository.create(store);

       this.storeRepository.createStoreUser(storeId, userId);
    }

    @Override
    public void createProfileImage(MultipartFile file, int storeId, int userId) throws InvalidOperationException, IOException {

        if (!this.authenticationService.isLoggedUser(userId)) {
            throw new InvalidOperationException(MessagesEnum.UNALLOWED.getMessage());
        }

        String imagePath = this.fileService.saveMultpartImage(file, "store", storeId);

        StoreModel store = this.storeRepository.getById(storeId);
        store.setProfileImagePath(imagePath);
        store.setLastUpdate(LocalDateTime.now());

        if (!this.storeRepository.update(store)) {
            throw new InvalidOperationException("Loja não encontrada!");
        }
    }

    @Override
    public List<StoreModel> getAllStores() {

        List<StoreModel> stores = this.storeRepository.getAll();

        if (stores != null) {
            stores.forEach((store) -> store.setAddress(this.addressService.getAdressById(store.getAddressId())));
        }

        return stores;
    }

    @Override
    public List<StoreModel> getStoresByUserId(int userId) throws InvalidOperationException {

        if (!this.authenticationService.isLoggedUser(userId)) {
            throw new InvalidOperationException(MessagesEnum.UNALLOWED.getMessage());
        }

        List<StoreModel> stores = this.storeRepository.getStoresByUserId(userId);

        if (stores != null) {
            stores.forEach((store) -> store.setAddress(this.addressService.getAdressById(store.getAddressId())));
        }

        return stores;
    }

    @Override
    public StoreModel getStoreById(int storeId) {

        StoreModel store = this.storeRepository.getById(storeId);

        return store;
    }

    @Override
    public String getProfileImage(int storeId, int userId, String path) throws InvalidOperationException, IOException {

        if (!this.authenticationService.isLoggedUser(userId)) {
            throw new InvalidOperationException(MessagesEnum.UNALLOWED.getMessage());
        }

        return this.fileService.getImageBase64(UriUtils.decode(path, "UTF-8"));
    }

    @Override
    public void updateStore(StoreModel store, int userId) throws InvalidOperationException {

        if (!this.authenticationService.isLoggedUser(userId)) {
            throw new InvalidOperationException(MessagesEnum.UNALLOWED.getMessage());
        }

        store.setLastUpdate(LocalDateTime.now());

        if (!this.storeRepository.update(store)) {
            throw new InvalidOperationException("Loja não encontrada!");
        }
    }

    @Override
    public void deleteStores(List<Integer> ids, int userId) {

        // TODO
    }
}